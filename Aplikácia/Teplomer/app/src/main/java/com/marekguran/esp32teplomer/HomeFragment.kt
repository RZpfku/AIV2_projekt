package com.marekguran.esp32teplomer

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.marekguran.esp32teplomer.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                    // Check if the database variable is not null
                    if (database != null) {
                        // Retrieve "teplota", "vlhkost", and "vzduch" data from Firebase and update the corresponding texts
                        val teplotaRef = database!!.getReference("data").child("teplota")
                        teplotaRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val teplota = dataSnapshot.value as? String ?: ""
                                binding?.teplotaValue?.text = teplota

                                // Extract the numerical value from the temperature string
                                val teplotaValue = teplota.substringBefore("℃").toFloatOrNull() ?: 0f

                                if (teplotaValue < 14f) {
                                    // Set the progress ring color to "zima"
                                    val colorResId = R.color.zima
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.temperatureDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                                if (teplotaValue >= 14f) {
                                    // Set the progress ring color to "chladno"
                                    val colorResId = R.color.chladno
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.temperatureDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                                if (teplotaValue >= 21f) {
                                    // Set the progress ring color to "normalna"
                                    val colorResId = R.color.normalna
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.temperatureDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                                if (teplotaValue >= 27f) {
                                    // Set the progress ring color to "teplo"
                                    val colorResId = R.color.teplo
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.temperatureDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
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
                                val vlhkostValue = vlhkost.substringBefore("%").toFloatOrNull() ?: 0f

                                if (vlhkostValue < 30f) {
                                    val colorResId = R.color.vlhkost_mala
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                                if (vlhkostValue >= 30f) {
                                    val colorResId = R.color.vlhkost_optimalna
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                                if (vlhkostValue >= 60f) {
                                    val colorResId = R.color.vlhkost_velka
                                    ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                    binding?.humidityDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle any errors that may occur while retrieving the data
                                // For example, you could log the error message using Log.e()
                            }
                        })

                        val vzduchRef = database!!.getReference("data").child("vzduch")
                        vzduchRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val vzduch = dataSnapshot.value as? String ?: ""
                                binding?.vzduchValue?.text = vzduch

                                val colorResId: Int
                                val airQuality: String

                                when (vzduch) {
                                    "Dobrá" -> {
                                        colorResId = R.color.vzduch_dobry
                                        airQuality = "Dobrá"
                                    }
                                    "Stredná" -> {
                                        colorResId = R.color.vzduch_stredny
                                        airQuality = "Stredná"
                                    }
                                    "Zlá" -> {
                                        colorResId = R.color.vzduch_zly
                                        airQuality = "Zlá"
                                    }
                                    else -> {
                                        colorResId = R.color.purple_200 // set default color if value is not recognized
                                        airQuality = ""
                                    }
                                }

                                ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring)?.setTint(ContextCompat.getColor(requireContext(), colorResId))
                                binding?.airDial?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.progress_ring))
                                binding?.vzduchValue?.text = airQuality
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
    }
}
