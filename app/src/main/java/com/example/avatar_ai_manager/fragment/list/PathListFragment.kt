package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.data.PathWithNames
import com.example.avatar_ai_manager.fragment.base.ListFragment

private const val TAG = "PathListFragment"

class PathListFragment : ListFragment<PathWithNames>() {

    private val args: PathListFragmentArgs by navArgs()

    private val addPath = {
        saveScrollPositionAndNavigate(
            PathListFragmentDirections.actionPathListFragmentToAddPathFragment(
                args.originId,
                args.originName,
                null,
                null,
                null
            )
        )
    }

    private val editPath = { path: PathWithNames ->
        saveScrollPositionAndNavigate(
            PathListFragmentDirections.actionPathListFragmentToEditPathFragment(
                args.originId,
                args.originName,
                path.getDestinationId(args.originId),
                path.getDestinationName(args.originId),
                path.distance.toString()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_path_list, args.originName),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_add_path),
                primaryButtonOnClick = addPath,
                isSecondaryButtonEnabled = false,
                secondaryButtonText = null,
                secondaryButtonOnClick = null
            )
        )

        setListFragmentOptions(
            ListOptions(
                header1Text = getString(R.string.header_path_destination),
                header2Text = getString(R.string.header_path_distance),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.getDestinationName(args.originId) },
                    getColumn2Text = { it.distance.toString() },
                    onClickedPrimary = editPath,
                    onClickedSecondary = null
                ),
                getFlowList = { databaseViewModel.getPathsWithNamesFromAnchor(args.originId) },
                navArgsScrollPosition = null
            )
        )

    }

}