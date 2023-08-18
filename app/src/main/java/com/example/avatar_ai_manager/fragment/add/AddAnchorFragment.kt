package com.example.avatar_ai_manager.fragment.add

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AddAnchorFragment"

class AddAnchorFragment : FormFragment() {

    private val anchorName get() = getPrimaryFieldText()
    private val anchorId get() = getSecondaryFieldText()

    private val discardAnchor: () -> Unit = {
        findNavController().navigateUp()
    }

    private val addAnchor: () -> Unit = {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                databaseViewModel.addAnchor(
                    Anchor(anchorId, anchorName)
                )
                withContext(Dispatchers.Main) {
                    showSnackBar(getString(R.string.message_anchor_added))
                    clearFields()
                }
            } catch (e: SQLiteConstraintException) {
                showSnackBar(getString(R.string.message_duplicate_error, anchorId))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_new_anchor),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_discard),
                primaryButtonOnClick = discardAnchor,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add),
                secondaryButtonOnClick = addAnchor
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = true,
                primaryTextFieldHint = getString(R.string.field_name),
                primaryTextFieldText = null,
                isSelectorEnabled = false,
                isSelectorEditable = null,
                selectorHint = null,
                getSelectorText = null,
                selectorOnClick = null,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_anchor_id),
                secondaryTextFieldText = null,
                isSwitchEnabled = false,
                switchText = null,
                getIsSwitchChecked = null
            )
        )

    }

    override fun onDestroyView() {
        if (anchorId.isNotEmpty() || anchorName.isNotEmpty()) {
            showSnackBar(getString(R.string.message_anchor_discarded))
        }
        super.onDestroyView()
    }

}