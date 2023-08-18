package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.data.AnchorWithPathCount
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment

private const val TAG = "PathListFragment"

class PathListFragment : ListWithMenuFragment<AnchorWithPathCount>() {

    private val showPathsAtAnchor = { anchor: AnchorWithPathCount ->
        saveScrollPositionAndNavigate(
            PathListFragmentDirections.actionPathListFragmentToPathsAtAnchorListFragment(
                anchor.id,
                anchor.name
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
                    onClickedPrimary = showPathsAtAnchor,
                    onClickedSecondary = null
                ),
                getFlowList = databaseViewModel::getAnchorsWithPathCounts
            )
        )

    }

}