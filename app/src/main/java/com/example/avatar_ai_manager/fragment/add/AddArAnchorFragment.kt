package com.example.avatar_ai_manager.fragment.add

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.databinding.FragmentAddArAnchorBinding
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
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

    /*
     * Sets up the AR scene view, and configures event listeners for
     * the avatar and host buttons.
     */
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

    /*
     * Handles the avatar button's click event.
     *
     * Depending on the current state of the avatar, this function either
     * loads the avatar into the AR scene or places it. The button's text is
     * updated accordingly to reflect the next action.
     */
    private fun setAvatarButtonOnClick() {
        // If avatar hasn't been loaded or is already placed
        if (!avatarIsLoaded || avatarIsPlaced) {
            // Remove the current avatar model node from the scene and destroy it
            modelNode?.let {
                sceneView.removeChild(it)
                it.destroy()
            }

            loadAvatar()

            // Update the button's text to indicate the next action is placing the avatar
            avatarButton.text = getString(R.string.button_place_avatar)

            avatarIsPlaced = false

        } else {
            placeAvatar()

            // Update the button's text to indicate the next action is loading a new avatar
            avatarButton.text = getString(R.string.button_summon_avatar)
        }
    }

    /*
    * Places the avatar in the AR scene by anchoring it.
    */
    private fun placeAvatar() {
        modelNode?.anchor()
        avatarIsPlaced = true
    }

    /*
     * Loads and displays an avatar in the AR scene.
     *
     * This function initializes an avatar model with predefined parameters
     * such as its file location, placement mode, and scale. Once initialized,
     * the avatar is added to the scene view and is set as the selected node.
     */
    private fun loadAvatar() {
        // Define the avatar's model details
        val avatar = Model(
            fileLocation = "models/robot_playground.glb",
            placementMode = PlacementMode.DISABLED,
            scale = 0.8f
        )

        // Create a node for the avatar in the AR scene with the desired placement mode
        modelNode = ArModelNode(
            sceneView.engine,
            PlacementMode.INSTANT,
        ).apply {
            // Asynchronously load the model in GLB format with specified attributes
            loadModelGlbAsync(
                glbFileLocation = avatar.fileLocation,
                centerOrigin = Position(y = -1.0f),
                scaleToUnits = avatar.scale
            )
        }
        sceneView.addChild(modelNode!!)

        // Set the loaded avatar as the currently selected node in the scene
        sceneView.selectedNode = modelNode

        avatarIsLoaded = true
    }

    /*
    * This function hosts the cloud anchor. It will display a
    * message to the user if there is not enough data to upload
    * the anchor.
    */
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

            // IMPORTANT: This hosts the anchor to the cloud
            val cloudAnchorFuture = modelNode?.anchor?.let { anchor ->
                sceneView.arSession?.hostCloudAnchorAsync(
                    anchor,
                    1,
                    null
                )
            }

            // Need to add in a loading screen while the anchor is loading so the user does not think it has glitched
            while (cloudAnchorFuture?.state == FutureState.PENDING) {
                // TODO: Log.d(TAG, "pending")
            }

            // Select cloud anchor ID and add to database
            val cloudAnchorId = cloudAnchorFuture?.resultCloudAnchorId
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
            try {
                viewModel.addAnchor(
                    Anchor(
                        anchorId,
                        ""
                    )
                )
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_anchor_added)
                )
            } catch (e: SQLiteConstraintException) {
                viewModel.showMessage(
                    requireActivity(),
                    getString(R.string.message_duplicate_error, anchorId)
                )
            }
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