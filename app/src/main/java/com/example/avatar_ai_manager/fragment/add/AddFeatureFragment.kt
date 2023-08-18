package com.example.avatar_ai_manager.fragment.add

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AddFeatureFragment"

class AddFeatureFragment : FormFragment() {

    private val args: AddFeatureFragmentArgs by navArgs()

    private val featureName get() = getPrimaryFieldText()
    private val featureDescription get() = getSecondaryFieldText()
    private val isPrimaryFeature get() = isSwitchChecked()

    private var navigatingToSelectAnchorFragment = false

    private val discardFeature: () -> Unit = {
        findNavController().navigateUp()
    }

    private val addFeature: () -> Unit = {
        if (!args.anchorId.isNullOrEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                addFeatureToDatabase()
                checkIfPrimaryFeature()

                withContext(Dispatchers.Main) {
                    showSnackBar(getString(R.string.message_feature_added))
                    clearFields()
                }
            }
        } else {
            showSnackBar(getString(R.string.message_anchor_required))
        }
    }

    private suspend fun addFeatureToDatabase() {
        try {
            databaseViewModel.addFeature(
                Feature(featureName, args.anchorId.toString(), featureDescription)
            )
        } catch (e: SQLiteConstraintException) {
            showSnackBar(getString(R.string.message_duplicate_error, featureName))
        }
    }

    private suspend fun checkIfPrimaryFeature() {
        if (isPrimaryFeature) {
            databaseViewModel.addPrimaryFeature(args.anchorId.toString(), featureName)
        } else {
            databaseViewModel.deletePrimaryFeature(featureName)
        }
    }

    private val selectAnchor = {
        navigatingToSelectAnchorFragment = true
        findNavController().navigate(
            AddFeatureFragmentDirections.actionAddFeatureFragmentToAnchorSelectionFragment(
                featureName,
                featureDescription,
                null,
                null,
                null
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_new_feature),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_discard),
                primaryButtonOnClick = discardFeature,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add),
                secondaryButtonOnClick = addFeature
            )
        )

        setFormFragmentOptions(
            FormOptions(
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = true,
                primaryTextFieldHint = getString(R.string.field_name),
                primaryTextFieldText = args.featureName,
                isSelectorEnabled = true,
                isSelectorEditable = true,
                selectorHint = getString(R.string.field_select_anchor),
                getSelectorText = {
                    args.anchorName?.let {
                        getString(R.string.field_anchor, it)
                    }
                },
                selectorOnClick = selectAnchor,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_description),
                secondaryTextFieldText = args.featureDescription,
                isSwitchEnabled = true,
                switchText = getString(R.string.field_primary_feature),
                getIsSwitchChecked = { true }
            )
        )
        navigatingToSelectAnchorFragment = false

    }

    override fun onDestroyView() {
        if ((featureName.isNotEmpty() || !args.anchorId.isNullOrEmpty() || featureDescription.isNotEmpty())
            && !navigatingToSelectAnchorFragment
        ) {
            showSnackBar(getString(R.string.message_feature_discarded))
        }
        super.onDestroyView()
    }

}