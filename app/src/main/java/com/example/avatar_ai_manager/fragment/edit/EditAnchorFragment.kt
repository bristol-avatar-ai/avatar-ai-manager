package com.example.avatar_ai_manager.fragment.edit

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment
import com.example.avatar_ai_manager.network.CloudAnchorApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "EditAnchorFragment"

class EditAnchorFragment : FormFragment() {

    private val args: EditAnchorFragmentArgs by navArgs()

    private val anchorName get() = getPrimaryFieldText()

    private val deleteAnchor: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            if (CloudAnchorApi.deleteAnchor(args.anchorId)) {
                databaseViewModel.deleteAnchor(args.anchorId)

                withContext(Dispatchers.Main) {
                    showSnackBar(getString(R.string.message_anchor_deleted))
                    findNavController().navigateUp()
                }
            } else {
                showSnackBar(getString(R.string.message_anchor_delete_failed))
                enableButtons()
            }
        }
    }

    private val updateAnchor: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.updateAnchor(args.anchorId, anchorName)

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_anchor_updated))
                enableButtons()
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
                isPrimaryTextFieldEditable = true,
                primaryTextFieldHint = getString(R.string.field_name),
                primaryTextFieldText = args.name,
                isSelectorEnabled = false,
                isSelectorEditable = null,
                selectorHint = null,
                getSelectorText = null,
                selectorOnClick = null,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = false,
                secondaryTextFieldHint = getString(R.string.field_anchor_id),
                secondaryTextFieldText = args.anchorId,
                isSwitchEnabled = false,
                switchText = null,
                getIsSwitchChecked = null
            )
        )

    }

    override fun onDestroyView() {
        if (anchorName != args.name) {
            showSnackBar(getString(R.string.message_changes_discarded))
        }
        super.onDestroyView()
    }

}