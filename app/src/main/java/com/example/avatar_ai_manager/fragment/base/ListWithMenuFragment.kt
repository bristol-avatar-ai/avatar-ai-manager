package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.avatar_ai_manager.R
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ListWithMenuFragment"

@Suppress("DEPRECATION")
abstract class ListWithMenuFragment<T> : ListFragment<T>() {

    data class MainListOptions(
        val switchScreenButtonTitle: String,
        val switchScreenButtonIcon: Int,
        val onSwitchScreen: () -> Unit
    )

    private var uploadMenuItem: MenuItem? = null
    private var refreshMenuItem: MenuItem? = null
    private var switchScreensMenuItem: MenuItem? = null

    private var switchScreenButtonTitle: String? = null
    private var switchScreenButtonIcon: Int? = null
    private var onSwitchScreen: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Enable custom options menu.
        setHasOptionsMenu(true)
    }

    protected fun setListWithMenuFragmentOptions(options: MainListOptions) {
        switchScreenButtonTitle = options.switchScreenButtonTitle
        switchScreenButtonIcon = options.switchScreenButtonIcon
        onSwitchScreen = options.onSwitchScreen
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        uploadMenuItem = menu.findItem(R.id.action_upload)
        refreshMenuItem = menu.findItem(R.id.action_refresh)
        switchScreensMenuItem = menu.findItem(R.id.action_switch_screen)

        switchScreensMenuItem?.title = switchScreenButtonTitle
        if (switchScreenButtonIcon != null) {
            switchScreensMenuItem?.setIcon(switchScreenButtonIcon!!)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun disableButtons() {
        super.disableButtons()
        uploadMenuItem?.isEnabled = false
        refreshMenuItem?.isEnabled = false
        switchScreensMenuItem?.isEnabled = false
    }

    override fun enableButtons() {
        super.enableButtons()
        uploadMenuItem?.isEnabled = true
        refreshMenuItem?.isEnabled = true
        switchScreensMenuItem?.isEnabled = true
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload -> onActionUpload()
            R.id.action_refresh -> onActionRefresh()
            R.id.action_switch_screen -> onActionSwitchScreen()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onActionUpload(): Boolean {
        if (viewModel.status.value == DatabaseViewModel.Status.READY) {
            disableButtons()
            confirmUploadAction()
        } else {
            showSnackBar(getString(R.string.message_upload_failure))
        }
        return true
    }

    private fun confirmUploadAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.button_upload_database))
            .setMessage(getString(R.string.message_upload_database))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.button_continue)) { _, _ ->
                uploadDatabase()
            }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ ->
                enableButtons()
            }
            .show()
    }

    private fun uploadDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (viewModel.uploadDatabase()) {
                showSnackBar(getString(R.string.message_upload_success))
            } else {
                showSnackBar(getString(R.string.message_upload_failure))
            }
            withContext(Dispatchers.Main) {
                enableButtons()
            }
        }
    }

    private fun onActionRefresh(): Boolean {
        if (viewModel.status.value != DatabaseViewModel.Status.LOADING) {
            disableButtons()
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.reload()
                withContext(Dispatchers.Main) {
                    enableButtons()
                }
            }
        }
        return true
    }

    private fun onActionSwitchScreen(): Boolean {
        onSwitchScreen?.invoke()
        return true
    }

}