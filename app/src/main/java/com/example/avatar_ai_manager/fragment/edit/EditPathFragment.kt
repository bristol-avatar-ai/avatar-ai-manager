package com.example.avatar_ai_manager.fragment.edit

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "EditPathFragment"

class EditPathFragment : FormFragment() {

    private val args: EditPathFragmentArgs by navArgs()

    private val distance get() = getSecondaryFieldText()

    private val deletePath: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.deletePath(args.originId, args.destinationId)

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_path_deleted))
                findNavController().navigateUp()
            }
        }
    }

    private val updatePath: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.updatePath(
                args.originId,
                args.destinationId,
                distance.toIntOrNull() ?: 0
            )
            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_path_updated))
                enableButtons()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_edit_path),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_delete),
                primaryButtonOnClick = deletePath,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_amend),
                secondaryButtonOnClick = updatePath
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = false,
                primaryTextFieldHint = getString(R.string.field_origin),
                primaryTextFieldText = args.originName,
                isSelectorEnabled = true,
                isSelectorEditable = false,
                selectorHint = null,
                getSelectorText = { getString(R.string.field_destination, args.destinationName) },
                selectorOnClick = null,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_distance),
                secondaryTextFieldText = args.distance,
                isSwitchEnabled = false,
                switchText = null,
                getIsSwitchChecked = null
            )
        )
        setSecondaryInputType(InputType.TYPE_CLASS_NUMBER)

    }

    override fun onDestroyView() {
        if (distance != args.distance) {
            showSnackBar(getString(R.string.message_changes_discarded))
        }
        super.onDestroyView()
    }
}