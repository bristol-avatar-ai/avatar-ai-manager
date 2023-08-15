package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment
import com.example.avatar_ai_manager.viewmodel.AnchorWithPathCount

private const val TAG = "AnchorPathsListFragment"

class AnchorPathsListFragment : ListWithMenuFragment<AnchorWithPathCount>() {

    private val args: AnchorPathsListFragmentArgs by navArgs()

    private val addArAnchor = {
        findNavController().navigate(
            AnchorPathsListFragmentDirections.actionAnchorPathsListFragmentToAddArAnchorFragment()
        )
    }

    private val addAnchorReference = {

    }

    private val showPathsList = { anchor: AnchorWithPathCount ->
        findNavController().navigate(
            AnchorPathsListFragmentDirections.actionAnchorPathsListFragmentToPathListFragment(anchor.id)
        )
    }

    private val showAnchorDescriptions = {
        findNavController().navigate(
            AnchorPathsListFragmentDirections.actionAnchorPathsListFragmentToAnchorDescriptionListFragment(
                getScrollPosition()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_anchor_list),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_add_ar_anchor),
                primaryButtonOnClick = addArAnchor,
                isSecondaryButtonEnabled = true,
                secondaryButtonText = getString(R.string.button_add_anchor),
                secondaryButtonOnClick = addAnchorReference
            )
        )

        setListFragmentOptions(
            ListOptions(
                header1Text = getString(R.string.header_anchor_id),
                header2Text = getString(R.string.header_anchor_path_number),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.id },
                    getColumn2Text = { it.pathCount.toString() },
                    onClickedPrimary = showPathsList,
                    onClickedSecondary = null
                ),
                getFlowList = viewModel::getAnchorsWithPathCounts,
                scrollPosition = args.scrollPosition
            )
        )

        setListWithMenuFragmentOptions(
            MainListOptions(
                onSwitchScreen = showAnchorDescriptions,
                switchScreenButtonTitle = getString(R.string.button_show_anchors),
                switchScreenButtonIcon = R.drawable.ic_anchor
            )
        )

    }

}