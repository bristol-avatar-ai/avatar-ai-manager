package com.example.ai_avatar_manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ai_avatar_manager.fragment.LoadingFragment

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)
    }

    /*
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Check if the current fragment is the loading fragment.
        val currentDestinationId = findNavController(R.id.nav_host_fragment).currentDestination?.id
        if (currentDestinationId == R.id.loadingFragment) {
            // If so, attempt to load the database.
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            val fragment =
                navHostFragment?.childFragmentManager?.findFragmentById(R.id.loadingFragment) as? LoadingFragment
            fragment?.loadDatabase()
            return false
        }

        // Enable the Up button for all other fragments.
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}