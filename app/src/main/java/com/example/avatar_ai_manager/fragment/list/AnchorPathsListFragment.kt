package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.data.AnchorWithPathCount
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment

private const val TAG = "AnchorPathsListFragment"

class AnchorPathsListFragment : ListWithMenuFragment<AnchorWithPathCount>() {

    private val args: AnchorPathsListFragmentArgs by navArgs()

    private val showPathsList = { anchor: AnchorWithPathCount ->
        saveScrollPositionAndNavigate(
            AnchorPathsListFragmentDirections.actionAnchorPathsListFragmentToPathListFragment(
                anchor.id,
                anchor.name
            )
        )
    }

    private val showAnchorDescriptions = {
        saveScrollPositionAndNavigate(
            AnchorPathsListFragmentDirections.actionAnchorPathsListFragmentToAnchorDescriptionListFragment(
                getScrollPosition().toString()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_paths_list),
                isPrimaryButtonEnabled = false,
                primaryButtonText = null,
                primaryButtonOnClick = null,
                isSecondaryButtonEnabled = false,
                secondaryButtonText = null,
                secondaryButtonOnClick = null
            )
        )

        setListFragmentOptions(
            ListOptions(
                header1Text = getString(R.string.header_anchor_name),
                header2Text = getString(R.string.header_anchor_path_number),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.name },
                    getColumn2Text = { it.pathCount.toString() },
                    onClickedPrimary = showPathsList,
                    onClickedSecondary = null
                ),
                getFlowList = databaseViewModel::getAnchorsWithPathCounts,
                navArgsScrollPosition = args.scrollPosition?.toIntOrNull()
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