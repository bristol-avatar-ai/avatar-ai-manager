package com.example.avatar_ai_manager.fragment.list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.adaptor.ClickableListAdaptor
import com.example.avatar_ai_manager.fragment.base.ListFragment

private const val TAG = "AnchorSelectionFragment"

class AnchorSelectionFragment : ListFragment<Anchor>() {

    private val args: AnchorSelectionFragmentArgs by navArgs()

    private val selectAnchor = { anchor: Anchor ->
        when (findNavController().previousBackStackEntry?.destination?.id) {
            R.id.addFeatureFragment -> navigateToAddFeatureFragment(anchor)
            R.id.editFeatureFragment -> navigateToEditFeatureFragment(anchor)
            R.id.addPathFragment -> navigateToAddPathFragment(anchor)
        }
    }

    private fun navigateToAddFeatureFragment(anchor: Anchor) {
        findNavController().navigate(
            AnchorSelectionFragmentDirections.actionAnchorSelectionFragmentToAddFeatureFragment(
                args.featureName,
                anchor.id,
                anchor.name,
                args.featureDescription
            )
        )
    }

    private fun navigateToEditFeatureFragment(anchor: Anchor) {
        findNavController().navigate(
            AnchorSelectionFragmentDirections.actionAnchorSelectionFragmentToEditFeatureFragment(
                args.featureName.toString(),
                anchor.id,
                anchor.name,
                args.featureDescription.toString()
            )
        )
    }

    private fun navigateToAddPathFragment(anchor: Anchor) {
        findNavController().navigate(
            AnchorSelectionFragmentDirections.actionAnchorSelectionFragmentToAddPathFragment(
                args.originId.toString(),
                args.originName.toString(),
                anchor.id,
                anchor.name,
                args.distance
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBaseFragmentOptions(
            BaseOptions(
                titleText = getString(R.string.title_select_anchor),
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
                header2Text = getString(R.string.header_anchor_id),
                listAdaptor = ClickableListAdaptor.create(
                    getColumn1Text = { it.name },
                    getColumn2Text = { it.id },
                    onClickedPrimary = { selectAnchor(it) },
                    onClickedSecondary = null
                ),
                getFlowList = databaseViewModel::getAnchors,
                navArgsScrollPosition = null
            )
        )

    }

}