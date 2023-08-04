package com.example.ai_avatar_manager.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
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

class AnchorListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

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
        setLayoutText()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setAdaptor()

        setAddAnchorButton()
        setUploadButton()
    }

    private fun setLayoutText() {
        binding.title.text = getString(R.string.title_anchor_list)
        binding.header1.text = getString(R.string.header_anchor_id)
        binding.header2.text = getString(R.string.header_description)
        binding.button1.text = getString(R.string.button_add_anchor)
        binding.button2.text = getString(R.string.button_upload_database)
    }

    private fun navigateToExhibitionList(anchorId: String) {
        val action =
            AnchorListFragmentDirections.actionAnchorListFragmentToExhibitionListFragment(anchorId)
        binding.root.findNavController().navigate(action)
    }

    private fun navigateToEditAnchor(anchorId: String, description: String) {
        val action =
            AnchorListFragmentDirections.actionAnchorListFragmentToEditAnchorFragment(
                anchorId,
                description
            )
        binding.root.findNavController().navigate(action)
    }

    private fun setAdaptor() {
        val anchorListAdapter = AnchorListAdaptor(
            ::navigateToExhibitionList,
            ::navigateToEditAnchor
        )
        recyclerView.adapter = anchorListAdapter

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAnchors().collect {
                (recyclerView.adapter as AnchorListAdaptor).submitList(it)
            }
        }
    }

    private fun setAddAnchorButton() {
        binding.button1.setOnClickListener {
            val action =
                AnchorListFragmentDirections.actionAnchorListFragmentToAddAnchorFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setUploadButton() {
        binding.button2.setOnClickListener {
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
    }

    private fun uploadDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
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
}