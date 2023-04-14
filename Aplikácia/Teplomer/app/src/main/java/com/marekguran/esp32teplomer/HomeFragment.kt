package com.marekguran.esp32teplomer

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.marekguran.esp32teplomer.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private var database: FirebaseDatabase? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private lateinit var teplotaValue: TextView
    private lateinit var vlhkostValue: TextView
    private lateinit var zap_vyp: Button
    private lateinit var wifi_off_teplota: ImageView
    private lateinit var wifi_off_vlhkost: ImageView

    private var isAttached: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        isAttached = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()


        binding?.let {
            teplotaValue = it.teplotaValue
            vlhkostValue = it.vlhkostValue
            wifi_off_teplota = it.wifiOffTeplota
            wifi_off_vlhkost = it.wifiOffVlhkost
            zap_vyp = it.zapVyp
        }

        // Vyvolané lebo sú to lateint, ktoré sa nevytvoria kým nebudú zavolané a ak by to nebolo, tak by checkInternetConnection() crashovalo aplikáciu


        // Check if the fragment is attached to the activity
        if (isAdded) {
            // Move the code that requires the context to a later point in the fragment lifecycle
            view?.post {
                checkInternetConnection()
            }
        }

        // Schedule the internet connectivity check to run every 15 seconds
        runnable = object : Runnable {
            override fun run() {
                checkInternetConnection()
                handler.postDelayed(this, 15000)
            }
        }
        handler.post(runnable)

        val vypZapButton = binding?.zapVyp
        vypZapButton?.setOnClickListener {
            if (isConnectedToInternet()) {
                val dataRef = database!!.getReference("data/displej")
                val value = if (it.isSelected) "0" else "1"
                it.isSelected = !it.isSelected
                dataRef.setValue(value)
                if (value == "0") {
                    Toast.makeText(context, "Obrazovka bola vypnutá", Toast.LENGTH_SHORT).show()
                    binding?.zapVyp?.text = "Vypnutá"
                } else {
                    Toast.makeText(context, "Obrazovka bola zapnutá", Toast.LENGTH_SHORT).show()
                    binding?.zapVyp?.text = "Zapnutá"
                }
            } else {
                Toast.makeText(context, "Bez pripojenia na internet", Toast.LENGTH_SHORT).show()
                binding?.zapVyp?.text = "Bez internetu"
            }
        }

        val dataRef = database!!.getReference("data/displej")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                vypZapButton?.isSelected = value == "1"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    // Check if the database variable is not null
                    if (database != null) {
                        // Retrieve "teplota", "vlhkost", and "vzduch" data from Firebase and update the corresponding texts
                        val teplotaRef = database!!.getReference("data").child("teplota")
                        teplotaRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (isAdded()) {
                                    val teplota = dataSnapshot.value as? String ?: ""
                                    binding?.teplotaValue?.text = teplota

                                    // Extract the numerical value from the temperature string
                                    val teplotaValue =
                                        teplota.substringBefore("℃").toFloatOrNull() ?: 0f

                                    if (teplotaValue < 14f) {
                                        // Set the progress ring color to "zima"
                                        val colorResId = R.color.zima
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )?.setTint(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                colorResId
                                            )
                                        )
                                        binding?.temperatureDial?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.progress_ring
                                            )
                                        )
                                    }
                                    if (teplotaValue >= 14f) {
                                        // Set the progress ring color to "chladno"
                                        val colorResId = R.color.chladno
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )?.setTint(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                colorResId
                                            )
                                        )
                                        binding?.temperatureDial?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.progress_ring
                                            )
                                        )
                                    }
                                    if (teplotaValue >= 21f) {
                                        // Set the progress ring color to "normalna"
                                        val colorResId = R.color.normalna
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )?.setTint(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                colorResId
                                            )
                                        )
                                        binding?.temperatureDial?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.progress_ring
                                            )
                                        )
                                    }
                                    if (teplotaValue >= 27f) {
                                        // Set the progress ring color to "teplo"
                                        val colorResId = R.color.teplo
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )?.setTint(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                colorResId
                                            )
                                        )
                                        binding?.temperatureDial?.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.progress_ring
                                            )
                                        )
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle any errors that may occur while retrieving the data
                                // For example, you could log the error message using Log.e()
                            }
                        })

                        val vlhkostRef = database!!.getReference("data").child("vlhkost")
                        vlhkostRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val vlhkost = dataSnapshot.value as? String ?: ""
                                binding?.vlhkostValue?.text = vlhkost


                                // Extract the numerical value from the temperature string
                                val vlhkostValue =
                                    vlhkost.substringBefore("%").toFloatOrNull() ?: 0f

                                if (vlhkostValue < 30f) {
                                    val colorResId = R.color.vlhkost_mala
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.progress_ring
                                    )?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )
                                    )
                                }
                                if (vlhkostValue >= 30f) {
                                    val colorResId = R.color.vlhkost_optimalna
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.progress_ring
                                    )?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )
                                    )
                                }
                                if (vlhkostValue >= 60f) {
                                    val colorResId = R.color.vlhkost_velka
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.progress_ring
                                    )?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.progress_ring
                                        )
                                    )
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle any errors that may occur while retrieving the data
                                // For example, you could log the error message using Log.e()
                            }
                        })

                        val tlakRef = database!!.getReference("data").child("tlak")
                        tlakRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val tlak = dataSnapshot.value as? String ?: ""
                                binding?.tlak?.text = "Tlak: " + tlak
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle any errors that may occur while retrieving the data
                                // For example, you could log the error message using Log.e()
                            }
                        })

                        val buttonRef = database!!.getReference("data").child("displej")
                        buttonRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val displej = dataSnapshot.value as? String ?: ""

                                if (displej == "1") {
                                    binding?.zapVyp?.text = "Zapnutá"
                                }
                                if (displej == "0") {
                                    binding?.zapVyp?.text = "Vypnutá"
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle any errors that may occur while retrieving the data
                                // For example, you could log the error message using Log.e()
                            }
                        })

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error occurred while retrieving data from Firebase: ${e.message}")
                }
            }


            override fun onLost(network: Network) {
                // Handle the case when the network connection is lost
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        database = null
        binding = null // ak bude zase crashovat aplikácia, tak odstrániť tento riadok, povodne má prečistiť pamäť keď sa prepne fragment alebo prejde do inej aplikácie
    }

    @Suppress("DEPRECATION")
    private fun checkInternetConnection() {
        val connectivityManager =
            activity?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                // If there is an internet connection, show the TextView and hide the ImageView
                teplotaValue.visibility = View.VISIBLE
                vlhkostValue.visibility = View.VISIBLE
                wifi_off_teplota.visibility = View.GONE
                wifi_off_vlhkost.visibility = View.GONE
            } else {
                // If there is no internet connection, hide the TextView and show the ImageView
                teplotaValue.visibility = View.GONE
                vlhkostValue.visibility = View.GONE
                wifi_off_teplota.visibility = View.VISIBLE
                wifi_off_vlhkost.visibility = View.VISIBLE
                binding?.zapVyp?.text = "Bez internetu"
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

}
