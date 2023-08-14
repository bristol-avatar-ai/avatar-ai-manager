package com.example.avatar_ai_manager.fragment.list

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
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.AnchorListAdaptor
import com.example.avatar_ai_manager.databinding.FragmentListBinding
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
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
        if (viewModel.status.value == DatabaseViewModel.Status.ERROR) {
            viewModel.init(requireContext())
        }
        // Enable custom options menu.
        setHasOptionsMenu(true)
        setLayoutText()
        setAddAnchorButton()
        setAddArAnchorButton()

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

    private fun addDatabaseObserver() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                DatabaseViewModel.Status.ERROR -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.loadingMessage.text = getString(R.string.message_network_error)
                    binding.loadingMessage.visibility = View.VISIBLE
                    (recyclerView.adapter as AnchorListAdaptor).submitList(emptyList())
                    disableButtons()
                }

                DatabaseViewModel.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loadingMessage.text = getString(R.string.message_loading)
                    binding.loadingMessage.visibility = View.VISIBLE
                    (recyclerView.adapter as AnchorListAdaptor).submitList(emptyList())
                    disableButtons()
                }

                DatabaseViewModel.Status.READY -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.loadingMessage.visibility = View.INVISIBLE
                    submitAdaptorList()
                    enableButtons()
                }
            }
        }
    }

    private fun enableButtons() {
        binding.button1.isEnabled = true
        binding.button2.isEnabled = true
    }

    private fun disableButtons() {
        binding.button1.isEnabled = false
        binding.button2.isEnabled = false
    }

    private fun submitAdaptorList() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAnchors().collect {
                (recyclerView.adapter as AnchorListAdaptor).submitList(it)
            }
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
                if (viewModel.status.value == DatabaseViewModel.Status.READY) {
                    item.isEnabled = false
                    uploadDatabaseConfirmation()
                    item.isEnabled = true
                }
                true
            }

            R.id.action_refresh -> {
                if (viewModel.status.value != DatabaseViewModel.Status.LOADING) {
                    refreshDatabase()
                }
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
        viewModel.reload(requireContext())
    }

}