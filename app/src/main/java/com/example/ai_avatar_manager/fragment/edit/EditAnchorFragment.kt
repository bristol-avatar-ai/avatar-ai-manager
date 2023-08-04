package com.example.ai_avatar_manager.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.databinding.FragmentEditDescriptionBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EditAnchorFragment"

private const val ARG_ANCHOR_ID = "anchorId"
private const val ARG_DESCRIPTION = "description"

class EditAnchorFragment : Fragment() {

    private var anchorId: String? = null
    private var description: String? = null

    private var _binding: FragmentEditDescriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anchorId = it.getString(ARG_ANCHOR_ID)
            description = it.getString(ARG_DESCRIPTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = getString(R.string.title_anchor_id, anchorId)
        binding.descriptionEditText.setText(description)
        anchorId?.let {
            setAmendButton(it)
            setDeleteButton(it)
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

    private fun setAmendButton(anchorId: String) {
        binding.button1.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.updateAnchor(anchorId, binding.descriptionEditText.text.toString())
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_description_amended)
                )
                enableButtons()
            }
        }
    }

    private fun setDeleteButton(anchorId: String) {
        binding.button2.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.deleteAnchor(anchorId)
                viewModel.showMessage(requireActivity(), getString(R.string.message_anchor_deleted))
                findNavController().navigateUp()
            }
        }
    }
}