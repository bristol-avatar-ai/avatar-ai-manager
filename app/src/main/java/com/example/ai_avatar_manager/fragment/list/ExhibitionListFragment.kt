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
import com.example.ai_avatar_manager.adaptor.ExhibitionListAdaptor
import com.example.ai_avatar_manager.databinding.FragmentListBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ExhibitionListFragment"

private const val ARG_ANCHOR_ID = "anchorId"

class ExhibitionListFragment : Fragment() {

    private var anchorId: String? = null

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

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
            setPathsButton(it)
            setAddButton(it)
        }
    }

    private fun setLayoutText() {
        binding.title.text = getString(R.string.title_exhibition_list, anchorId)
        binding.header1.text = getString(R.string.header_exhibition_name)
        binding.header2.text = getString(R.string.header_exhibition_description)
        binding.button1.text = getString(R.string.button_paths)
        binding.button2.text = getString(R.string.button_add_exhibition)
    }

    private fun navigateToEditExhibition(name: String, description: String) {
        val action =
            ExhibitionListFragmentDirections.actionExhibitionListFragmentToEditExhibitionFragment(
                name,
                description
            )
        binding.root.findNavController().navigate(action)
    }

    private fun setAdaptor() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val exhibitionListAdaptor = ExhibitionListAdaptor(::navigateToEditExhibition)
        recyclerView.adapter = exhibitionListAdaptor

        lifecycleScope.launch(Dispatchers.Main) {
            anchorId?.let { anchorId ->
                viewModel.getExhibitionsAtAnchor(anchorId).collect() {
                    exhibitionListAdaptor.submitList(it)
                }
            }
        }
    }

    private fun setPathsButton(anchorId: String) {
        binding.button1.setOnClickListener {
            val action =
                ExhibitionListFragmentDirections.actionExhibitionListFragmentToPathListFragment(
                    anchorId
                )
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setAddButton(anchorId: String) {
        binding.button2.setOnClickListener {
            val action =
                ExhibitionListFragmentDirections.actionExhibitionListFragmentToAddExhibitionFragment(
                    anchorId
                )
            binding.root.findNavController().navigate(action)
        }
    }
}