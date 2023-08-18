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

private const val TAG = "EditFeatureFragment"

class EditFeatureFragment : FormFragment() {

    private val args: EditFeatureFragmentArgs by navArgs()

    private val featureDescription get() = getSecondaryFieldText()
    private val isPrimaryFeature get() = isSwitchChecked()

    private val deleteFeature: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.deleteFeature(args.featureName)
            databaseViewModel.deletePrimaryFeature(args.featureName)

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_feature_deleted))
                findNavController().navigateUp()
            }
        }
    }

    private val updateFeature: () -> Unit = {
        disableButtons()
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.updateFeature(args.featureName, featureDescription)
            checkIfPrimaryFeature()

            withContext(Dispatchers.Main) {
                showSnackBar(getString(R.string.message_feature_updated))
                enableButtons()
            }
        }
    }

    private suspend fun checkIfPrimaryFeature() {
        databaseViewModel.deletePrimaryFeature(args.featureName)
        if (isPrimaryFeature) {
            databaseViewModel.addPrimaryFeature(args.anchorId, args.featureName)
        }
    }

    private val selectAnchor = {
        findNavController().navigate(
            EditFeatureFragmentDirections.actionEditFeatureFragmentToAnchorSelectionFragment(
                args.featureName,
                featureDescription
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_edit_feature),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_delete),
                primaryButtonOnClick = deleteFeature,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_amend),
                secondaryButtonOnClick = updateFeature
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = false,
                primaryTextFieldHint = getString(R.string.field_name),
                primaryTextFieldText = args.featureName,
                isSelectorEnabled = true,
                isSelectorEditable = true,
                selectorHint = null,
                getSelectorText = {
                    getString(
                        R.string.field_anchor,
                        args.anchorName ?: databaseViewModel.getAnchor(args.anchorId)?.name
                    )
                },
                selectorOnClick = selectAnchor,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_description),
                secondaryTextFieldText = args.featureDescription,
                isSwitchEnabled = true,
                switchText = getString(R.string.field_primary_feature),
                getIsSwitchChecked = {
                    databaseViewModel.isPrimaryFeature(
                        args.featureName,
                        args.anchorId
                    )
                }
            )
        )

    }

}