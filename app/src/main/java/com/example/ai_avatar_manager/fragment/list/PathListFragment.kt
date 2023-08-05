package com.example.ai_avatar_manager.fragment.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.adaptor.PathListAdaptor
import com.example.ai_avatar_manager.databinding.FragmentListBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PathListFragment"

private const val ARG_ANCHOR_ID = "anchorId"

class PathListFragment : Fragment() {

    private var anchorId: String? = null

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private val navigateToEditPathFragment =
        { originId: String, destinationId: String, distance: Int ->
            binding.root.findNavController().navigate(
                PathListFragmentDirections.actionPathListFragmentToEditPathFragment(
                    originId,
                    destinationId,
                    distance
                )
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anchorId = it.getString(ARG_ANCHOR_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutText()
        setAdaptor()
        anchorId?.let {
            setExhibitionsButton(it)
            setAddButton(it)
        }
    }

    private fun setLayoutText() {
        binding.title.text = getString(R.string.title_path_list, anchorId)
        binding.header1.text = getString(R.string.header_path_destination)
        binding.header2.text = getString(R.string.header_path_distance)
        binding.button1.text = getString(R.string.button_exhibitions)
        binding.button2.text = getString(R.string.button_add_path)
    }

    private fun setAdaptor() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val pathListAdaptor = PathListAdaptor(navigateToEditPathFragment)
        recyclerView.adapter = pathListAdaptor

        lifecycleScope.launch(Dispatchers.Main) {
            anchorId?.let { anchorId ->
                viewModel.getPathsFromAnchor(anchorId).collect() {
                    pathListAdaptor.submitList(it)
                }
            }
        }
    }

    private fun setExhibitionsButton(anchorId: String) {
        binding.button1.setOnClickListener {
            val action =
                PathListFragmentDirections.actionPathListFragmentToExhibitionListFragment(anchorId)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setAddButton(anchorId: String) {
        binding.button2.setOnClickListener {
            val action =
                PathListFragmentDirections.actionPathListFragmentToAddPathFragment(
                    anchorId, null
                )
            binding.root.findNavController().navigate(action)
        }
    }
}