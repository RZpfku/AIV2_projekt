package com.marekguran.esp32teplomer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.database.*

private val tag = "Database"

class DatabaseData(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "data_table"
        private const val COL_ID = "id"
        private const val COL_TEPL = "teplota"
        private const val COL_VLHK = "vlhkost"
        private const val COL_VZDU = "vzduch"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TEPL INTEGER, $COL_VLHK INTEGER, $COL_VZDU INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(teplota: Int, vlhkost: Int, vzduch: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_TEPL, teplota)
        contentValues.put(COL_VLHK, vlhkost)
        contentValues.put(COL_VZDU, vzduch)
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != (-1).toLong()
    }

    fun getAllData(): List<String> {
        val data = ArrayList<String>()

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COL_ID", null)

        if (cursor.moveToFirst()) {
            do {
                val teplota = cursor.getInt(1)
                val vlhkost = cursor.getInt(2)
                val vzduch = cursor.getInt(3)
                data.add("$teplota, $vlhkost, $vzduch")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun getIdFromData(data: String): Int {
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT $COL_ID FROM $TABLE_NAME WHERE $COL_TEPL = '$data'", null)

        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }

        cursor.close()
        Log.d(tag, "Got ID")
        return id
    }

    fun deleteData(id: Int): Boolean {
        val db = this.writableDatabase
        Log.d(tag, "Before delete: " + getAllData().toString())
        val result = db.delete(TABLE_NAME, "$COL_ID = $id", null)
        Log.d(tag, "After delete: " + getAllData().toString())
        Log.d(tag, "database data deletion, result: $result")
        return result > 0
    }

    fun deleteDatabase(context: Context): Boolean {
        return context.deleteDatabase(DATABASE_NAME)
    }


    fun listenForDataChanges() {
        Log.d(tag, "listenForDataChanges()")
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("data")

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                insertDataFromSnapshot(dataSnapshot)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                updateDataFromSnapshot(dataSnapshot)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                deleteDataFromSnapshot(dataSnapshot)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // not implemented
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(tag, "Firebase data download failed: $databaseError")
            }
        })
    }

    fun insertDataFromSnapshot(dataSnapshot: DataSnapshot) {
        Log.d(tag, "insertDataFromSnapshot()")
        val teplota = dataSnapshot.child("teplota").value as String
        val vzduch = dataSnapshot.child("vzduch").value as String
        val vlhkost = dataSnapshot.child("vlhkost").value as String
        val contentValues = ContentValues()
        contentValues.put(COL_TEPL, teplota)
        contentValues.put(COL_VLHK, vlhkost)
        contentValues.put(COL_VZDU, vzduch)

        val db = this.writableDatabase
        val cursor = db.query(TABLE_NAME, null, "$COL_TEPL = ? AND $COL_VLHK = ? AND $COL_VZDU = ?", arrayOf(teplota, vlhkost, vzduch), null, null, null)
        val dataExists = cursor.count > 0
        cursor.close()

        if (!dataExists) {
            db.insert(TABLE_NAME, null, contentValues)
        }
    }

    fun updateDataFromSnapshot(dataSnapshot: DataSnapshot) {
        Log.d(tag, "updateDataFromSnapshot()")
        cleanLocalDatabase()
        listenForDataChanges()
    }

    fun deleteDataFromSnapshot(dataSnapshot: DataSnapshot) {
        Log.d(tag, "deleteDataFromSnapshot()")
        val teplota = dataSnapshot.child("teplota").value as String
        val vlhkost = dataSnapshot.child("vlhkost").value as String
        val vzduch = dataSnapshot.child("vzduch").value as String
        val db = this.writableDatabase
        cleanLocalDatabase()
        this.writableDatabase.delete(TABLE_NAME, "$COL_TEPL = ? AND $COL_VLHK = ? AND $COL_VZDU = ?", arrayOf(teplota, vlhkost, vzduch))
    }

    fun cleanLocalDatabase() {
        Log.d(tag, "cleanLocalDatabase()")
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }




}