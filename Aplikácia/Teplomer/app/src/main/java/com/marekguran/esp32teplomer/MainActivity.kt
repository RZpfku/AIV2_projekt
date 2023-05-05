package com.marekguran.esp32teplomer

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.marekguran.esp32teplomer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the current device orientation
        val currentOrientation = resources.configuration.orientation

        // Check if the device is in landscape orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Use the NavigationRailView instead of the BottomNavigationView
            val navView: NavigationRailView = binding.navView as NavigationRailView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_about))

            navView.setupWithNavController(navController)
        } else {
            // Use the BottomNavigationView for portrait orientation
            val navView: BottomNavigationView = binding.navView as BottomNavigationView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_about))

            navView.setupWithNavController(navController)
        }
    }

}