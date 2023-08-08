package com.example.avatar_ai_manager.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.databinding.FragmentEditDescriptionBinding
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EditExhibitionFragment"

private const val ARG_NAME = "name"
private const val ARG_DESCRIPTION = "description"

class EditExhibitionFragment : Fragment() {

    private var name: String? = null
    private var description: String? = null

    private var _binding: FragmentEditDescriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME)
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
        binding.title.text = getString(R.string.title_exhibition_name, name)
        binding.descriptionEditText.setText(description)
        name?.let {
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

    private fun setAmendButton(name: String) {
        binding.button1.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.updateExhibition(name, binding.descriptionEditText.text.toString())
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_description_amended)
                )
                enableButtons()
            }
        }
    }

    private fun setDeleteButton(name: String) {
        binding.button2.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.deleteExhibition(name)
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_exhibition_deleted)
                )
                findNavController().navigateUp()
            }
        }
    }
}