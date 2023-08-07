package com.example.ai_avatar_manager.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.databinding.FragmentLoadingBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "LoadingScreenFragment"

class LoadingFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private var requireNavigationOnResume = false
    private val navigationAction: NavDirections =
        LoadingFragmentDirections.actionLoadingFragmentToAnchorListFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDatabase()
        addDatabaseObserver()
    }

    fun loadDatabase() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textView.text = getString(R.string.message_loading)
        viewModel.init(requireContext())
    }

    private fun addDatabaseObserver() {
        viewModel.isReady.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    Log.i(TAG, "Database loaded successfully")
                    navigateToAnchorList()
                }

                false -> showNetworkError()
                else -> {}
            }
        }
    }

    /*
    * This function sets the navigation action to be run onResume
    * if the FragmentManager has saved the state of the fragment.
     */
    private fun navigateToAnchorList() {
        if (requireActivity().supportFragmentManager.isStateSaved) {
            requireNavigationOnResume = true
        } else {
            binding.root.findNavController().navigate(navigationAction)
        }
    }

    private fun showNetworkError() {
        Log.e(TAG, "Database failed to load")
        binding.progressBar.visibility = View.INVISIBLE
        binding.textView.text = getString(R.string.title_network_error)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_network_error))
            .setMessage(getString(R.string.message_network_error))
            .setPositiveButton(getString(R.string.button_network_error)) { _, _ -> }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (requireNavigationOnResume) {
            binding.root.findNavController().navigate(navigationAction)
        }
    }

}