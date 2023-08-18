package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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

    private var uploadMenuItem: MenuItem? = null
    private var refreshMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable custom options menu.
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        uploadMenuItem = menu.findItem(R.id.action_upload)
        refreshMenuItem = menu.findItem(R.id.action_refresh)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDatabaseLoading() {
        super.onDatabaseLoading()
        uploadMenuItem?.isEnabled = false
        refreshMenuItem?.isEnabled = false
    }

    override fun onDatabaseReady() {
        super.onDatabaseReady()
        uploadMenuItem?.isEnabled = true
        refreshMenuItem?.isEnabled = true
    }

    override fun onDatabaseError() {
        super.onDatabaseError()
        uploadMenuItem?.isEnabled = false
        refreshMenuItem?.isEnabled = true
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload -> onActionUpload()
            R.id.action_refresh -> onActionRefresh()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onActionUpload(): Boolean {
        if (databaseViewModel.status.value == DatabaseViewModel.Status.READY) {
            disableButtons()
            confirmUploadAction()
        } else {
            showSnackBar(getString(R.string.message_upload_failure))
        }
        return true
    }

    private fun confirmUploadAction() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_upload_database))
            .setMessage(getString(R.string.message_upload_database))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.button_continue)) { _, _ ->
                upload()
            }
            .setNegativeButton(getString(R.string.button_cancel)) { _, _ ->
                enableButtons()
            }
            .show()
    }

    private fun upload() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (databaseViewModel.upload()) {
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
        if (databaseViewModel.status.value != DatabaseViewModel.Status.LOADING) {
            lifecycleScope.launch(Dispatchers.IO) {
                databaseViewModel.reload()
            }
        }
        return true
    }

}