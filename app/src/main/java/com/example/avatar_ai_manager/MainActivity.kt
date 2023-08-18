package com.example.avatar_ai_manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.avatar_ai_manager.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

private const val SNACK_BAR_DURATION = 2000

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    lateinit var snackBar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupTopNavigationBar()
        setupBottomNavigationBar()

        // Create SnackBar for displaying messages.
        snackBar = Snackbar.make(binding.root, "", SNACK_BAR_DURATION)
    }

    private fun setupTopNavigationBar() {
        // Set up the top action bar for use with the NavController
        setupActionBarWithNavController(
            navController,
            // Specify the bottom menu items as top-level destinations.
            AppBarConfiguration.Builder(binding.bottomNavView.menu).build()
        )
    }

    @Suppress("DEPRECATION")
    private fun setupBottomNavigationBar() {
        // Set up the bottom bar for use with the NavController
        binding.bottomNavView.setupWithNavController(navController)
        binding.bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.featureListFragment, R.id.anchorListFragment, R.id.pathListFragment -> {
                    navController.popBackStack()
                    navController.navigate(menuItem.itemId)
                    true
                }

                else -> false
            }
        }
    }

    /*
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Enable the Up button for all other fragments.
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}