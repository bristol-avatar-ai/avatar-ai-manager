package com.example.ai_avatar_manager.fragment.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.database.Exhibition
import com.example.ai_avatar_manager.databinding.FragmentAddExhibitionBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AddExhibitionFragment"

private const val ARG_ANCHOR_ID = "anchorId"

class AddExhibitionFragment : Fragment() {

    private var anchorId: String? = null

    private var _binding: FragmentAddExhibitionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

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
        _binding = FragmentAddExhibitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = getString(R.string.title_anchor_id, anchorId)
        anchorId?.let {
            setAddButton(it)
        }
        setDiscardButton()
    }

    private fun enableButtons() {
        binding.button1.isEnabled = true
        binding.button2.isEnabled = true
    }

    private fun disableButtons() {
        binding.button1.isEnabled = false
        binding.button2.isEnabled = false
    }

    private fun setAddButton(anchorId: String) {
        binding.button1.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                val name = binding.nameEditText.text.toString()
                val description = binding.descriptionEditText.text.toString()
                viewModel.addExhibition(Exhibition(name, anchorId, description))
                viewModel.showMessage(requireActivity(), getString(R.string.message_exhibition_added))
                binding.nameEditText.text?.clear()
                binding.descriptionEditText.text?.clear()
                enableButtons()
            }
        }
    }

    private fun setDiscardButton() {
        binding.button2.setOnClickListener() {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.showMessage(requireActivity(), getString(R.string.message_exhibition_discarded))
                findNavController().navigateUp()
            }
        }
    }
}