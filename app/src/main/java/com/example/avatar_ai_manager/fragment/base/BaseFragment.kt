package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.avatar_ai_manager.MainActivity
import com.example.avatar_ai_manager.databinding.FragmentBaseBinding
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModel
import com.example.avatar_ai_manager.viewmodel.DatabaseViewModelFactory
import com.example.avatar_ai_manager.viewmodel.UiStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "BaseFragment"

abstract class BaseFragment : Fragment() {

    data class BaseOptions(
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

    protected lateinit var databaseViewModel: DatabaseViewModel

    protected val uiStateViewModel: UiStateViewModel by viewModels()

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
        databaseViewModel = ViewModelProvider(
            requireActivity(),
            DatabaseViewModelFactory(requireActivity().application)
        )[DatabaseViewModel::class.java]
        addDatabaseObserver()
    }

    protected open fun addDatabaseObserver() {
        databaseViewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                null -> onDatabaseNull()
                DatabaseViewModel.Status.ERROR -> onDatabaseError()
                DatabaseViewModel.Status.LOADING -> onDatabaseLoading()
                DatabaseViewModel.Status.READY -> onDatabaseReady()
            }
        }
    }

    private fun onDatabaseNull() {
        lifecycleScope.launch(Dispatchers.IO) {
            databaseViewModel.reload()
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

    protected fun setBaseFragmentOptions(options: BaseOptions) {
        outerBinding.title.text = options.titleText
        setPrimaryButton(options)
        setSecondaryButton(options)
    }

    private fun setPrimaryButton(options: BaseOptions) {
        if (options.isPrimaryButtonEnabled) {
            outerBinding.buttonPrimary.text = options.primaryButtonText
            outerBinding.buttonPrimary.setOnClickListener {
                options.primaryButtonOnClick?.invoke()
            }
            outerBinding.buttonPrimary.visibility = View.VISIBLE
        }
    }

    private fun setSecondaryButton(options: BaseOptions) {
        if (options.isSecondaryButtonEnabled) {
            outerBinding.buttonSecondary.text = options.secondaryButtonText
            outerBinding.buttonSecondary.setOnClickListener {
                options.secondaryButtonOnClick?.invoke()
            }
            outerBinding.buttonSecondary.visibility = View.VISIBLE
        }
    }

    protected open fun enableButtons() {
        outerBinding.buttonPrimary.isEnabled = true
        outerBinding.buttonSecondary.isEnabled = true
    }

    protected open fun disableButtons() {
        outerBinding.buttonPrimary.isEnabled = false
        outerBinding.buttonSecondary.isEnabled = false
    }

    protected fun showSnackBar(message: String) {
        (requireActivity() as MainActivity).snackBar.setText(message).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _outerBinding = null
    }

}