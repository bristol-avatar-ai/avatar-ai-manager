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
import com.example.ai_avatar_manager.databinding.FragmentPathBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EditPathFragment"

private const val ARG_ORIGIN_ID = "originId"
private const val ARG_DESTINATION_ID = "destinationId"
private const val ARG_DISTANCE = "distance"

class EditPathFragment : Fragment() {

    private var originId: String? = null
    private var destinationId: String? = null
    private var distance: Int? = null

    private var _binding: FragmentPathBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originId = it.getString(ARG_ORIGIN_ID)
            destinationId = it.getString(ARG_DESTINATION_ID)
            distance = it.getInt(ARG_DISTANCE)
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
        binding.destination.text = destinationId
        binding.distanceEditText.setText(distance.toString())
        binding.button1.text = getString(R.string.button_amend)
        binding.button2.text = getString(R.string.button_delete)

        originId?.let { originId ->
            destinationId?.let { destinationId ->
                setAmendButton(originId, destinationId)
                setDeleteButton(originId, destinationId)
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

    private fun setAmendButton(originId: String, destinationId: String) {
        binding.button1.setOnClickListener {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                amendPath(originId, destinationId)
                enableButtons()
            }
        }
    }

    private suspend fun amendPath(originId: String, destinationId: String) {
        viewModel.updatePath(
            originId,
            destinationId,
            binding.distanceEditText.text.toString().toIntOrNull() ?: 0
        )
        viewModel.showMessage(
            requireActivity(),
            getString(R.string.message_path_amended)
        )
    }

    private fun setDeleteButton(originId: String, destinationId: String) {
        binding.button2.setOnClickListener() {
            disableButtons()
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.deletePath(originId, destinationId)
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_path_deleted)
                )
                findNavController().navigateUp()
            }
        }
    }
}