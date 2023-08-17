package com.example.avatar_ai_manager.fragment.edit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "EditAnchorFragment"

class EditAnchorFragment : FormFragment() {

    private val args: EditAnchorFragmentArgs by navArgs()

    private val anchorDescription get() = getSecondaryFieldText()

    private val deleteAnchor: () -> Unit = {
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.deleteAnchor(args.anchorId)

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_anchor_deleted))
                findNavController().navigateUp()
            }
        }
    }

    private val updateAnchor: () -> Unit = {
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.updateAnchor(args.anchorId, anchorDescription)

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_anchor_updated))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_edit_anchor),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_delete),
                primaryButtonOnClick = deleteAnchor,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_amend),
                secondaryButtonOnClick = updateAnchor
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = false,
                primaryTextFieldHint = getString(R.string.field_anchor_id),
                primaryTextFieldText = args.anchorId,
                isSelectorEnabled = false,
                isSelectorEditable = null,
                selectorText = null,
                selectorOnClick = null,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_description),
                secondaryTextFieldText = args.description,
                isSwitchEnabled = false,
                switchText = null
            )
        )

    }

    override fun onDestroyView() {
        if(anchorDescription != args.description){
            showSnackBar(getString(R.string.message_changes_discarded))
        }
        super.onDestroyView()
    }

}