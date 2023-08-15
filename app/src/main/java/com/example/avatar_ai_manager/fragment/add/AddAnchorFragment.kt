package com.example.avatar_ai_manager.fragment.add

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.fragment.base.FormFragment

private const val TAG = "AddAnchorFragment"

class AddAnchorFragment : FormFragment() {

    private val args: AddAnchorFragmentArgs by navArgs()

    private val showAnchorDescriptions = {
        findNavController().navigate(
            AddAnchorFragmentDirections.actionAddAnchorFragmentToAnchorDescriptionListFragment(args.scrollPosition)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_add_anchor),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_discard),
                primaryButtonOnClick = {},
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add_anchor),
                secondaryButtonOnClick = {}
            )
        )

        setFormFragmentOptions(
            FormOptions(
                onBackPressed = showAnchorDescriptions,
                isPrimaryTextFieldEnabled = true,
                isPrimaryTextFieldEditable = true,
                primaryTextFieldHint = getString(R.string.field_anchor_id),
                primaryTextFieldText = null,
                isSelectorEnabled = false,
                isSelectorEditable = null,
                selectorText = null,
                isSecondaryTextFieldEnabled = true,
                isSecondaryTextFieldEditable = true,
                secondaryTextFieldHint = getString(R.string.field_description),
                secondaryTextFieldText = null,
                isSwitchEnabled = false,
                switchText = null
            )
        )

    }
}