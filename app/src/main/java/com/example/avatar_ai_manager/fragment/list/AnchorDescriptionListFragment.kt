package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment

private const val TAG = "AnchorDescriptionListFragment"

class AnchorDescriptionListFragment : ListWithMenuFragment<Anchor>() {

    private val args: AnchorDescriptionListFragmentArgs by navArgs()

    private val addArAnchor = {
        findNavController().navigate(
            AnchorDescriptionListFragmentDirections.actionAnchorDescriptionListFragmentToAddArAnchorFragment()
        )
    }

    private val addAnchorReference = {
        findNavController().navigate(
            AnchorDescriptionListFragmentDirections.actionAnchorDescriptionListFragmentToAddAnchorFragment(
                getScrollPosition()
            )
        )
    }

    private val editDescriptions = { _: Anchor -> }

    private val showAnchorPaths: () -> Unit = {
        findNavController().navigate(
            AnchorDescriptionListFragmentDirections.actionAnchorDescriptionListFragmentToAnchorPathsListFragment(
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
                header2Text = getString(R.string.header_anchor_description),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.id },
                    getColumn2Text = { it.description },
                    onClickedPrimary = editDescriptions,
                    onClickedSecondary = null
                ),
                getFlowList = viewModel::getAnchors,
                scrollPosition = args.scrollPosition
            )
        )

        setListWithMenuFragmentOptions(
            MainListOptions(
                onSwitchScreen = showAnchorPaths,
                switchScreenButtonTitle = getString(R.string.button_show_paths),
                switchScreenButtonIcon = R.drawable.ic_path
            )
        )

    }

}