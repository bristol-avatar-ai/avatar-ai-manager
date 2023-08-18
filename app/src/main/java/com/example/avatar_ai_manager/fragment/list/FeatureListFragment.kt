package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment

private const val TAG = "FeatureListFragment"

class FeatureListFragment : ListWithMenuFragment<Feature>() {

    private val addFeature = {
        saveScrollPositionAndNavigate(
            FeatureListFragmentDirections.actionFeatureListFragmentToAddFeatureFragment(
                null,
                null,
                null,
                null
            )
        )
    }

    private val editFeature = { feature: Feature ->
        saveScrollPositionAndNavigate(
            FeatureListFragmentDirections.actionFeatureListFragmentToEditFeatureFragment(
                feature.name,
                feature.anchor,
                null,
                feature.description
            )
        )
    }

    private val showAnchorDescriptions = {
        saveScrollPositionAndNavigate(
            FeatureListFragmentDirections.actionFeatureListFragmentToAnchorDescriptionListFragment(
                null
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_feature_list),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_add_feature),
                primaryButtonOnClick = addFeature,
                isSecondaryButtonEnabled = false,
                secondaryButtonText = null,
                secondaryButtonOnClick = null
            )
        )

        setListFragmentOptions(
            ListOptions(
                header1Text = getString(R.string.header_feature_name),
                header2Text = getString(R.string.header_feature_description),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.name },
                    getColumn2Text = { it.description },
                    onClickedPrimary = editFeature,
                    onClickedSecondary = null
                ),
                getFlowList = databaseViewModel::getFeatures,
                navArgsScrollPosition = null
            )
        )

        setListWithMenuFragmentOptions(
            MainListOptions(
                switchScreenButtonTitle = getString(R.string.button_show_anchors),
                switchScreenButtonIcon = R.drawable.ic_anchor,
                onSwitchScreen = showAnchorDescriptions
            )
        )

    }

}