package com.example.ai_avatar_manager.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.databinding.FragmentAddBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AddAnchorFragment"

class AddAnchorFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = getString(R.string.add_anchor_fragment_label)
        binding.icon1.setImageResource(R.drawable.ic_anchor)
        binding.field1.hint = getString(R.string.field_anchor_id)
        setAddButton()
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

    private fun setAddButton() {
        binding.button1.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                addAnchor()
                enableButtons()
            }
        }
    }

    private suspend fun addAnchor() {
        viewModel.addAnchor(
            com.example.avatar_ai_cloud_storage.database.Anchor(
                binding.field1EditText.text.toString(),
                binding.descriptionEditText.text.toString()
            )
        )
        viewModel.showMessage(
            requireActivity(),
            getString(R.string.message_anchor_added)
        )
        binding.field1EditText.text?.clear()
        binding.descriptionEditText.text?.clear()
    }

    private fun setDiscardButton() {
        binding.button2.setOnClickListener() {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_anchor_discarded)
                )
                findNavController().navigateUp()
            }
        }
    }
}