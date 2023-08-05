package com.example.ai_avatar_manager.fragment.add

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ai_avatar_manager.R
import com.example.ai_avatar_manager.database.Anchor
import com.example.ai_avatar_manager.databinding.FragmentAddArAnchorBinding
import com.example.ai_avatar_manager.viewmodel.DatabaseViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import com.google.ar.core.FutureState
import com.google.ar.core.Session
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "AddAnchorFragment"

class AddArAnchorFragment : Fragment() {

    private var _binding: FragmentAddArAnchorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DatabaseViewModel by activityViewModels()

    private lateinit var sceneView: ArSceneView
    private lateinit var avatarButton: ExtendedFloatingActionButton
    private lateinit var hostButton: ExtendedFloatingActionButton

    data class Model(
        val fileLocation: String,
        val placementMode: PlacementMode = PlacementMode.BEST_AVAILABLE,
        val scale: Float
    )

    private var modelNode: ArModelNode? = null
    private var avatarIsLoaded: Boolean = false
    private var avatarIsPlaced: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddArAnchorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initArFragment()

        sceneView.apply {
            cloudAnchorEnabled = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initArFragment() {
        sceneView = binding.sceneView.apply {
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            planeRenderer.isVisible = false
        }
        avatarButton = binding.avatarButton.apply {
            setOnClickListener { setAvatarButtonOnClick() }
        }
        hostButton = binding.hostButton.apply {
            setOnClickListener { hostButtonOnClick(requireContext()) }
        }
    }

    private fun setAvatarButtonOnClick() {
        if (!avatarIsLoaded || avatarIsPlaced) {
            modelNode?.let {
                sceneView.removeChild(it)
                it.destroy()
            }
            loadAvatar()
            avatarButton.text = getString(R.string.button_place_avatar)
            avatarIsPlaced = false
        } else {
            placeAvatar()
            avatarButton.text = getString(R.string.button_summon_avatar)
        }

    }

    private fun placeAvatar() {
        modelNode?.anchor()
        avatarIsPlaced = true
    }

    private fun loadAvatar() {
        val avatar = Model(
            fileLocation = "models/robot_playground.glb",
            placementMode = PlacementMode.DISABLED,
            scale = 0.8f
        )

        modelNode = ArModelNode(
            sceneView.engine,
            PlacementMode.INSTANT,
        ).apply {
            loadModelGlbAsync(
                glbFileLocation = avatar.fileLocation,
                centerOrigin = Position(y = -1.0f),
                scaleToUnits = avatar.scale
            )
        }
        sceneView.addChild(modelNode!!)
        sceneView.selectedNode = modelNode
        avatarIsLoaded = true
    }

    private fun hostButtonOnClick(context: Context) {
        if (!avatarIsPlaced) {
            Toast.makeText(context, "Please place the model first", Toast.LENGTH_SHORT).show()
        } else {
            val frame = sceneView.arSession?.update() ?: return
            if (sceneView.arSession?.estimateFeatureMapQualityForHosting(frame.camera.pose) == Session.FeatureMapQuality.INSUFFICIENT) {
                Toast.makeText(
                    context,
                    "Insufficient data to host, please move the device around the model",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            Log.d(TAG, "hosting begining:")

            // IMPORTANT: This uploads the cloud anchor
            val cloudAnchorFuture = modelNode?.anchor?.let { anchor ->
                sceneView.arSession?.hostCloudAnchorAsync(
                    anchor,
                    1,
                    null
                )
            }

            Log.d(TAG, "hosting func over")

            while (cloudAnchorFuture?.state == FutureState.PENDING) {
                // TODO: Log.d(TAG, "pending")
            }

            Log.d(TAG, "pending over")

            val cloudAnchorId = cloudAnchorFuture?.resultCloudAnchorId
            val cloudState = cloudAnchorFuture?.state

            Log.d(TAG, "Cloud Anchor ID: $cloudAnchorId, State: $cloudState")

            if (cloudAnchorId != null) {
                Log.d(TAG, "ID: $cloudAnchorId")
            }
            processCloudAnchor(cloudAnchorId)
        }

    }

    /*
    * This function adds the anchor ID to the database if it
    * has been generated successfully. Otherwise, it shows
    * an error message and navigates back to the anchor list.
     */
    private fun processCloudAnchor(anchorId: String?) {
        if (anchorId != null) {
            addAnchorToDatabase(anchorId)
        } else {
            viewModel.showMessage(
                requireActivity(),
                getString(R.string.message_anchor_failed)
            )
            // Navigate back to AnchorListFragment
            findNavController().navigateUp()
        }
    }

    /*
    * This function adds the anchor ID to the database and shows
    * a success message.
     */
    private fun addAnchorToDatabase(anchorId: String) {
        // Add anchor to database in IO thread.
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addAnchor(Anchor(anchorId, ""))
            viewModel.showMessage(
                requireActivity(),
                getString(R.string.message_anchor_added)
            )
            navigateToEditAnchorFragment(anchorId)
        }
    }

    /*
    * This function navigates to the edit screen of the newly added anchor.
     */
    private suspend fun navigateToEditAnchorFragment(anchorId: String) {
        // Switch back to main thread.
        withContext(Dispatchers.Main) {
            // Navigate to AnchorEditFragment
            binding.root.findNavController().navigate(
                AddArAnchorFragmentDirections.actionAddAnchorFragmentToEditAnchorFragment(anchorId)
            )
        }
    }

}