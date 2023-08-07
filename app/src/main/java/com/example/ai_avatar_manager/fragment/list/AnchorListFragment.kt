package com.example.ai_avatar_manager.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.adaptor.AnchorListAdaptor
import com.example.ai_avatar_manager.databinding.FragmentListBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AnchorListFragment"

@Suppress("DEPRECATION")
class AnchorListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private val navigateToExhibitionList = { anchorId: String ->
        binding.root.findNavController().navigate(
            AnchorListFragmentDirections.actionAnchorListFragmentToExhibitionListFragment(anchorId)
        )
    }

    private val navigateToEditAnchor = { anchorId: String, description: String ->
        binding.root.findNavController().navigate(
            AnchorListFragmentDirections.actionAnchorListFragmentToEditAnchorFragment(
                anchorId,
                description
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Enable custom options menu.
        setHasOptionsMenu(true)
        setLayoutText()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val anchorListAdapter = AnchorListAdaptor(
            navigateToExhibitionList, navigateToEditAnchor
        )
        recyclerView.adapter = anchorListAdapter
        addDatabaseObserver()
    }

    private fun setLayoutText() {
        binding.title.text = getString(R.string.title_anchor_list)
        binding.header1.text = getString(R.string.header_anchor_id)
        binding.header2.text = getString(R.string.header_description)
        binding.button1.text = getString(R.string.button_add_anchor)
        binding.button2.text = getString(R.string.button_add_ar_anchor)
    }

    private fun addDatabaseObserver() {
        viewModel.isReady.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    submitAdaptorList()
                    setAddAnchorButton()
                    setAddArAnchorButton()
                }

                else -> {
                    // Navigate to loading fragment if database is not ready.
                    binding.root.findNavController().navigate(
                        AnchorListFragmentDirections.actionAnchorListFragmentToLoadingFragment()
                    )
                }
            }
        }
    }

    private fun submitAdaptorList() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAnchors().collect {
                (recyclerView.adapter as AnchorListAdaptor).submitList(it)
            }
        }
    }

    private fun setAddAnchorButton() {
        binding.button1.setOnClickListener {
            binding.root.findNavController()
                .navigate(AnchorListFragmentDirections.actionAnchorListFragmentToAddAnchorFragment())
        }
    }

    private fun setAddArAnchorButton() {
        binding.button2.setOnClickListener {
            binding.root.findNavController()
                .navigate(AnchorListFragmentDirections.actionAnchorListFragmentToAddArAnchorFragment())
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload -> {
                item.isEnabled = false
                uploadDatabaseConfirmation()
                item.isEnabled = true
                true
            }

            R.id.action_refresh -> {
                refreshDatabase()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun uploadDatabaseConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.button_upload_database))
            .setMessage(getString(R.string.message_upload_database))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.button_continue)) { _, _ ->
                uploadDatabase()
            }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ -> }
            .show()
    }

    private fun uploadDatabase() {
        requireActivity().lifecycleScope.launch(Dispatchers.IO) {
            if (viewModel.uploadDatabase(requireContext())) {
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_upload_success)
                )
            } else {
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_upload_failure)
                )
            }

        }
    }

    private fun refreshDatabase() {
        viewModel.close(requireContext())
    }

}