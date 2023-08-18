package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListWithMenuFragment

private const val TAG = "AnchorListFragment"

class AnchorListFragment : ListWithMenuFragment<Anchor>() {

    private val args: AnchorListFragmentArgs by navArgs()

    private val addArAnchor = {
        saveScrollPositionAndNavigate(
            AnchorListFragmentDirections.actionAnchorListFragmentToAddArAnchorFragment()
        )
    }

    private val addAnchorReference = {
        saveScrollPositionAndNavigate(
            AnchorListFragmentDirections.actionAnchorListFragmentToAddAnchorFragment()
        )
    }

    private val editAnchor = { anchor: Anchor ->
        saveScrollPositionAndNavigate(
            AnchorListFragmentDirections.actionAnchorListFragmentToEditAnchorFragment(
                anchor.id,
                anchor.name
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
                header1Text = getString(R.string.header_anchor_name),
                header2Text = getString(R.string.header_anchor_id),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.name },
                    getColumn2Text = { it.id },
                    onClickedPrimary = { editAnchor(it) },
                    onClickedSecondary = null
                ),
                getFlowList = databaseViewModel::getAnchors,
                navArgsScrollPosition = args.scrollPosition?.toIntOrNull()
            )
        )

    }

}