package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.avatar_ai_manager.MainActivity
import com.example.avatar_ai_manager.databinding.ActivityMainBinding
import com.example.avatar_ai_manager.databinding.FragmentBaseBinding
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "BaseFragment"

private const val SNACK_BAR_DURATION = 2000
private const val SNACK_BAR_MAX_LINES = 1
private const val SNACK_BAR_HEIGHT = 120

abstract class BaseFragment : Fragment() {

    data class Options(
        val titleText: String,
        val isPrimaryButtonEnabled: Boolean,
        val primaryButtonText: String?,
        val primaryButtonOnClick: (() -> Unit)?,
        val isSecondaryButtonEnabled: Boolean,
        val secondaryButtonText: String?,
        val secondaryButtonOnClick: (() -> Unit)?
    )

    private var _outerBinding: FragmentBaseBinding? = null
    protected val outerBinding get() = _outerBinding!!

    protected lateinit var viewModel: DatabaseViewModel

    private lateinit var snackBar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _outerBinding = FragmentBaseBinding.inflate(inflater, container, false)
        return outerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialise DatabaseViewModel
        viewModel = ViewModelProvider(
            this,
            DatabaseViewModelFactory(requireActivity().application)
        )[DatabaseViewModel::class.java]
        addDatabaseObserver()

        // Initialise Snack Bar
        val activityBinding = (requireActivity() as MainActivity).binding
        val navHostFragmentParams =
            activityBinding.navHostFragment.layoutParams as ViewGroup.MarginLayoutParams
        snackBar = createSnackBar(activityBinding, navHostFragmentParams)
    }

    private fun addDatabaseObserver() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                null -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.reload()
                    }
                }

                DatabaseViewModel.Status.ERROR -> onDatabaseError()
                DatabaseViewModel.Status.LOADING -> onDatabaseLoading()
                DatabaseViewModel.Status.READY -> onDatabaseReady()
            }
        }
    }

    protected open fun onDatabaseError() {
        disableButtons()
    }

    protected open fun onDatabaseLoading() {
        disableButtons()
    }

    protected open fun onDatabaseReady() {
        enableButtons()
    }

    private fun createSnackBar(
        activityBinding: ActivityMainBinding,
        navHostFragmentParams: ViewGroup.MarginLayoutParams
    ): Snackbar {
        return Snackbar.make(activityBinding.root, "", SNACK_BAR_DURATION)
            .addCallback(object : Snackbar.Callback() {

                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                    navHostFragmentParams.bottomMargin += SNACK_BAR_HEIGHT
                    activityBinding.navHostFragment.layoutParams = navHostFragmentParams
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    navHostFragmentParams.bottomMargin -= SNACK_BAR_HEIGHT
                    activityBinding.navHostFragment.layoutParams = navHostFragmentParams
                }

            })
            .setTextMaxLines(SNACK_BAR_MAX_LINES)
    }

    protected fun setBaseFragmentOptions(options: Options) {
        outerBinding.title.text = options.titleText
        setPrimaryButton(options)
        setSecondaryButton(options)
    }

    private fun setPrimaryButton(options: Options) {
        if (options.isPrimaryButtonEnabled) {
            outerBinding.buttonPrimary.text = options.primaryButtonText
            outerBinding.buttonPrimary.setOnClickListener {
                options.primaryButtonOnClick?.invoke()
            }
            outerBinding.buttonPrimary.visibility = View.VISIBLE
        }
    }

    private fun setSecondaryButton(options: Options) {
        if (options.isSecondaryButtonEnabled) {
            outerBinding.buttonSecondary.text = options.secondaryButtonText
            outerBinding.buttonSecondary.setOnClickListener {
                options.secondaryButtonOnClick?.invoke()
            }
            outerBinding.buttonSecondary.visibility = View.VISIBLE
        }
    }

    protected fun showSnackBar(message: String) {
        snackBar.setText(message)
        snackBar.show()
    }

    protected open fun enableButtons() {
        outerBinding.buttonPrimary.isEnabled = true
        outerBinding.buttonSecondary.isEnabled = true
    }

    protected open fun disableButtons() {
        outerBinding.buttonPrimary.isEnabled = false
        outerBinding.buttonSecondary.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _outerBinding = null
    }
}