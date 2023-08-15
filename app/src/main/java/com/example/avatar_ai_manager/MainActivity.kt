package com.example.avatar_ai_manager

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.avatar_ai_manager.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

private const val SNACK_BAR_DURATION = 2000
private const val SNACK_BAR_MAX_LINES = 1
private const val SNACK_BAR_HEIGHT = 120

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

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

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)

        // Create SnackBar for displaying messages.
        val navHostFragmentParams =
            binding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
        snackBar = createSnackBar(navHostFragmentParams)
    }

    /*
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Enable the Up button for all other fragments.
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun createSnackBar(
        navHostFragmentParams: ViewGroup.MarginLayoutParams
    ): Snackbar {
        return Snackbar.make(binding.root, "", SNACK_BAR_DURATION)
            .addCallback(object : Snackbar.Callback() {

                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                    navHostFragmentParams.bottomMargin += SNACK_BAR_HEIGHT
                    binding.navHostFragment.layoutParams = navHostFragmentParams
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    navHostFragmentParams.bottomMargin -= SNACK_BAR_HEIGHT
                    binding.navHostFragment.layoutParams = navHostFragmentParams
                }

            })
            .setTextMaxLines(SNACK_BAR_MAX_LINES)
    }

}