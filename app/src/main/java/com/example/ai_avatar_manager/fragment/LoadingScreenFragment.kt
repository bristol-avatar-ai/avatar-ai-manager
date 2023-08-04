package com.example.ai_avatar_manager.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.databinding.FragmentLoadingScreenBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModelCallBack
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoadingScreenFragment"

class LoadingScreenFragment : Fragment(), DatabaseViewModelCallBack {

    private var _binding: FragmentLoadingScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private var pendingNavigationAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(requireContext(), this)
    }

    override fun onDatabaseViewModelInit(status: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (status) {
                DatabaseViewModelCallBack.SUCCESS -> {
                    if(isStateSaved) {
                        pendingNavigationAction = ::navigateToAnchorListFragment
                    } else {
                        navigateToAnchorListFragment()
                    }
                }
                else -> showNetworkError()
            }
        }
    }

    private fun navigateToAnchorListFragment() {
        Log.i(TAG, "Database loaded successfully")
        val action =
            LoadingScreenFragmentDirections.actionLoadingScreenFragmentToAnchorListFragment()
        binding.root.findNavController().graph.setStartDestination(R.id.anchorListFragment)
        binding.root.findNavController().navigate(action)
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
        pendingNavigationAction?.let {
            navigateToAnchorListFragment()
            pendingNavigationAction = null
        }
    }

}