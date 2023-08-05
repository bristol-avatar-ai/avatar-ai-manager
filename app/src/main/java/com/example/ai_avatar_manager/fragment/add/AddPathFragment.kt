package com.example.ai_avatar_manager.fragment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.database.Path
import com.example.ai_avatar_manager.databinding.FragmentPathBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AddPathFragment"

private const val ARG_ORIGIN_ID = "originId"
private const val ARG_DESTINATION_ID = "destinationId"

class AddPathFragment : Fragment() {

    private var originId: String? = null
    private var destinationId: String? = null

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originId = it.getString(ARG_ORIGIN_ID)
            destinationId = it.getString(ARG_DESTINATION_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = getString(R.string.title_origin, originId)
        if (destinationId == null) {
            binding.destination.text = getString(R.string.button_select_destination)
        } else {
            binding.destination.text = destinationId
        }
        binding.button1.text = getString(R.string.button_add)
        binding.button2.text = getString(R.string.button_discard)

        originId?.let { originId ->
            setSelectPathButton(originId)
            destinationId?.let { destinationId ->
                setAddButton(originId, destinationId)
            }
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

    private fun setSelectPathButton(originId: String) {
        binding.destination.setOnClickListener {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                binding.root.findNavController().navigate(
                    AddPathFragmentDirections.actionAddPathFragmentToPathSelectionFragment(originId)
                )
            }
        }
    }

    private fun setAddButton(originId: String, destinationId: String) {
        binding.button1.setOnClickListener {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                addPath(originId, destinationId)
                enableButtons()
            }
        }
    }

    private suspend fun addPath(originId: String, destinationId: String) {
        viewModel.addPath(
            Path(
                originId,
                destinationId,
                binding.distanceEditText.text.toString().toIntOrNull() ?: 0
            )
        )
        viewModel.showMessage(
            requireActivity(),
            getString(R.string.message_path_added)
        )
        binding.destination.text = getString(R.string.button_select_destination)
        this.destinationId = null
        binding.distanceEditText.text?.clear()
    }

    private fun setDiscardButton() {
        binding.button2.setOnClickListener() {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_path_discarded)
                )
                findNavController().navigateUp()
            }
        }
    }
}