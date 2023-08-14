package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.AnchorListAdaptor
import com.example.avatar_ai_manager.fragment.base.MainListFragment

private const val TAG = "AnchorListFragment"

class AnchorListFragment : MainListFragment<Anchor, AnchorListAdaptor.AnchorListViewHolder>() {

    private val onAnchorClicked = { _: String ->
    }

    private val onDescriptionClicked = { _: String, _: String ->
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBaseFragmentOptions(
            Options(
                titleText = getString(R.string.title_anchor_list),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_add_ar_anchor),
                primaryButtonOnClick = {},
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add_anchor),
                secondaryButtonOnClick = {}
            )
        )
        setListFragmentOptions(
            Options(
                header1Text = getString(R.string.header_anchor_id),
                header2Text = getString(R.string.header_description),
                listAdaptor = AnchorListAdaptor(
                    onAnchorClicked, onDescriptionClicked
                ),
                getFlowList = viewModel::getAnchors
            )
        )
        setMainListFragmentOptions(
            Options(
                onSwitchScreen = {},
                switchScreenButtonTitle = getString(R.string.button_show_features),
                switchScreenButtonIcon = R.drawable.ic_feature
            )
        )
    }

}