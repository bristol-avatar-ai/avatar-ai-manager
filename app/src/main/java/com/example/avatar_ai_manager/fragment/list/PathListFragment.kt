package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_cloud_storage.database.entity.Path
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListFragment
import com.example.avatar_ai_manager.viewmodel.getDestinationId

private const val TAG = "PathListFragment"

class PathListFragment : ListFragment<Path>() {

    private val args: PathListFragmentArgs by navArgs()

    private val editPath = { _: Path -> }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_path_list, args.originId),
                isPrimaryButtonEnabled = true,
                primaryButtonText = getString(R.string.button_add_path),
                primaryButtonOnClick = {},
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
                    getColumn1Text = { it.getDestinationId(args.originId) },
                    getColumn2Text = { it.distance.toString() },
                    onClickedPrimary = editPath,
                    onClickedSecondary = null
                ),
                getFlowList = { databaseViewModel.getPathsFromAnchor(args.originId) },
                navArgsScrollPosition = null
            )
        )

    }

}