package com.example.ai_avatar_manager.fragment

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
import com.example.ai_avatar_manager.adaptor.PathSelectionAdaptor
import com.example.ai_avatar_manager.databinding.FragmentListBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PathSelectionFragment"

private const val ARG_ORIGIN_ID = "originId"

class PathSelectionFragment : Fragment() {

    private var originId: String? = null

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private val selectedPathNavigation = { originId: String, destinationId: String ->
        binding.root.findNavController().navigate(
            PathSelectionFragmentDirections.actionPathSelectionFragmentToAddPathFragment(
                originId,
                destinationId
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originId = it.getString(ARG_ORIGIN_ID)
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

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        originId?.let {
            setAdaptor(it)
            setCancelButton(it)
        }
    }

    private fun setLayoutText() {
        binding.title.text = getString(R.string.title_anchor_list)
        binding.header1.text = getString(R.string.header_path_destination)
        binding.header2.text = getString(R.string.header_path_distance)
        binding.button1.visibility = View.GONE
        binding.button2.text = getString(R.string.button_cancel)
    }

    private fun setAdaptor(originId: String) {
        val pathSelectionAdaptor = PathSelectionAdaptor(
            originId, selectedPathNavigation
        )
        recyclerView.adapter = pathSelectionAdaptor

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAnchors().collect {
                pathSelectionAdaptor.submitList(it)
            }
        }
    }

    private fun setCancelButton(originId: String) {
        binding.button2.setOnClickListener {
            binding.root.findNavController().navigate(
                PathSelectionFragmentDirections.actionPathSelectionFragmentToAddPathFragment(
                    originId,
                    null
                )
            )
        }
    }
}