package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.avatar_ai_manager.databinding.FragmentFormBinding

private const val TAG = "FormFragment"

@Suppress("DEPRECATION")
abstract class FormFragment : BaseFragment() {

    data class FormOptions(
        val onBackPressed: (() -> Unit),
        val isPrimaryTextFieldEnabled: Boolean,
        val isPrimaryTextFieldEditable: Boolean?,
        val primaryTextFieldHint: String?,
        val primaryTextFieldText: String?,
        val isSelectorEnabled: Boolean,
        val isSelectorEditable: Boolean?,
        val selectorText: String?,
        val isSecondaryTextFieldEnabled: Boolean,
        val isSecondaryTextFieldEditable: Boolean?,
        val secondaryTextFieldHint: String?,
        val secondaryTextFieldText: String?,
        val isSwitchEnabled: Boolean,
        val switchText: String?
    )

    private var _innerBinding: FragmentFormBinding? = null
    private val innerBinding get() = _innerBinding!!

    private var onBackPressed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _innerBinding = FragmentFormBinding.inflate(inflater, outerBinding.fragmentContainer, true)
        return outerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed?.invoke()
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    protected fun setFormFragmentOptions(options: FormOptions) {
        onBackPressed = options.onBackPressed
        setPrimaryTextField(options)
        setSelector(options)
        setSecondaryTextField(options)
        setSwitchSelector(options)
    }

    private fun setPrimaryTextField(options: FormOptions) {
        if (options.isPrimaryTextFieldEnabled) {
            innerBinding.textFieldPrimary.visibility = View.VISIBLE
            options.isPrimaryTextFieldEditable?.let { innerBinding.textFieldPrimary.isEnabled = it }
            innerBinding.textFieldPrimary.hint = options.primaryTextFieldHint
            innerBinding.textEditPrimary.setText(options.primaryTextFieldText)
        }
    }

    private fun setSelector(options: FormOptions) {
        if (options.isSelectorEnabled) {
            innerBinding.selector.visibility = View.VISIBLE
            options.isSelectorEditable?.let { innerBinding.selector.isEnabled = it }
            innerBinding.selector.text = options.selectorText
        }
    }

    private fun setSecondaryTextField(options: FormOptions) {
        if (options.isSecondaryTextFieldEnabled) {
            innerBinding.textFieldSecondary.visibility = View.VISIBLE
            options.isSecondaryTextFieldEditable?.let {
                innerBinding.textFieldSecondary.isEnabled = it
            }
            innerBinding.textFieldSecondary.hint = options.secondaryTextFieldHint
            innerBinding.textEditSecondary.setText(options.secondaryTextFieldText)
        }
    }

    private fun setSwitchSelector(options: FormOptions) {
        if (options.isSwitchEnabled) {
            innerBinding.switchSelector.visibility = View.VISIBLE
            innerBinding.switchSelector.text = options.switchText
        }
    }

    protected fun getPrimaryFieldText(): String {
        return innerBinding.textEditPrimary.text.toString()
    }

    protected fun getSelectorText(): String {
        return innerBinding.selector.text.toString()
    }

    protected fun getSecondaryFieldText(): String {
        return innerBinding.textEditSecondary.text.toString()
    }

    protected fun isSwitchChecked(): Boolean {
        return innerBinding.switchSelector.isChecked
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _innerBinding = null
    }

}