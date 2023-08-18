package com.example.avatar_ai_manager.fragment.add

import android.database.sqlite.SQLiteConstraintException
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

private const val TAG = "AddPathFragment"

private const val PATH = "path"

class AddPathFragment : FormFragment() {

    private val args: AddPathFragmentArgs by navArgs()

    private val distance get() = getSecondaryFieldText()

    private var isNotDiscardAction = false

    private val discardPath: () -> Unit = {
        findNavController().navigateUp()
    }

    private val addPath: () -> Unit = {
        lifecycleScope.launch(Dispatchers.Main) {
            if (args.destinationId != null) {
                disableButtons()
                try {
                    addPathToDatabase()
                    isNotDiscardAction = true
                    findNavController().navigateUp()
                } catch (e: SQLiteConstraintException) {
                    showSnackBar(getString(R.string.message_duplicate_error, PATH))
                }
                enableButtons()
            } else {
                showSnackBar(getString(R.string.message_destination_required))
            }
        }
    }

    private suspend fun addPathToDatabase() {
        withContext(Dispatchers.IO) {
            databaseViewModel.addPath(
                args.originId,
                args.destinationId.toString(),
                distance.toIntOrNull() ?: 0
            )
        }
        showSnackBar(getString(R.string.message_path_added))
    }

    private val selectDestination = {
        isNotDiscardAction = true
        findNavController().navigate(
            AddPathFragmentDirections.actionAddPathFragmentToAnchorSelectionFragment(
                null,
                null,
                args.originId,
                args.originName,
                distance
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_new_path),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_discard),
                primaryButtonOnClick = discardPath,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add),
                secondaryButtonOnClick = addPath
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = false,
                primaryTextFieldHint = getString(R.string.field_origin),
                primaryTextFieldText = args.originName,
                isSelectorEnabled = true,
                isSelectorEditable = true,
                selectorHint = getString(R.string.field_select_destination),
                getSelectorText = {
                    args.destinationName?.let {
                        getString(R.string.field_destination, it)
                    }
                },
                selectorOnClick = selectDestination,
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
        isNotDiscardAction = false

    }

    override fun onDestroyView() {
        if ((!args.destinationId.isNullOrEmpty() || distance.isNotEmpty())
            && !isNotDiscardAction
        ) {
            showSnackBar(getString(R.string.message_path_discarded))
        }
        super.onDestroyView()
    }

}