package com.example.avatar_ai_manager.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.avatar_ai_manager.databinding.FragmentFormBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "FormFragment"

abstract class FormFragment : BaseFragment() {

    data class FormOptions(
        val isPrimaryTextFieldEnabled: Boolean,
        val isPrimaryTextFieldEditable: Boolean?,
        val primaryTextFieldHint: String?,
        val primaryTextFieldText: String?,
        val isSelectorEnabled: Boolean,
        val isSelectorEditable: Boolean?,
        val selectorHint: String?,
        val getSelectorText: (suspend () -> String?)?,
        val selectorOnClick: (() -> Unit)?,
        val isSecondaryTextFieldEnabled: Boolean,
        val isSecondaryTextFieldEditable: Boolean?,
        val secondaryTextFieldHint: String?,
        val secondaryTextFieldText: String?,
        val isSwitchEnabled: Boolean,
        val switchText: String?,
        val getIsSwitchChecked: (suspend () -> Boolean)?
    )

    private var _innerBinding: FragmentFormBinding? = null
    private val innerBinding get() = _innerBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _innerBinding = FragmentFormBinding.inflate(inflater, outerBinding.fragmentContainer, true)
        return outerBinding.root
    }

    protected fun setFormFragmentOptions(options: FormOptions) {
        setPrimaryTextField(options)
        setSelector(options)
        setSecondaryTextField(options)
        setSwitchSelector(options)
    }

    private fun setPrimaryTextField(options: FormOptions) {
        if (options.isPrimaryTextFieldEnabled) {
            innerBinding.textFieldPrimary.visibility = View.VISIBLE
            innerBinding.textFieldPrimary.isEnabled = options.isPrimaryTextFieldEditable ?: true
            innerBinding.textFieldPrimary.hint = options.primaryTextFieldHint
            innerBinding.textEditPrimary.setText(options.primaryTextFieldText)
        }
    }

    private fun setSelector(options: FormOptions) {
        if (options.isSelectorEnabled) {
            innerBinding.selector.visibility = View.VISIBLE
            innerBinding.selector.isEnabled = options.isSelectorEditable ?: true
            innerBinding.selector.hint = options.selectorHint
            options.getSelectorText?.let { getSelectorText ->
                lifecycleScope.launch(Dispatchers.IO) {
                    innerBinding.selector.text = getSelectorText()
                }
            }
            options.selectorOnClick?.let {
                innerBinding.selector.setOnClickListener { it() }
            }
        }
    }

    private fun setSecondaryTextField(options: FormOptions) {
        if (options.isSecondaryTextFieldEnabled) {
            innerBinding.textFieldSecondary.visibility = View.VISIBLE
            innerBinding.textFieldSecondary.isEnabled = options.isSecondaryTextFieldEditable ?: true
            innerBinding.textFieldSecondary.hint = options.secondaryTextFieldHint
            innerBinding.textEditSecondary.setText(options.secondaryTextFieldText)
        }
    }

    private fun setSwitchSelector(options: FormOptions) {
        if (options.isSwitchEnabled) {
            innerBinding.switchSelector.visibility = View.VISIBLE
            innerBinding.switchSelector.text = options.switchText
            options.getIsSwitchChecked?.let { getIsSwitchChecked ->
                lifecycleScope.launch(Dispatchers.IO) {
                    innerBinding.switchSelector.isChecked = getIsSwitchChecked()
                }
            }
        }
    }

    protected fun getPrimaryFieldText(): String {
        return innerBinding.textEditPrimary.text.toString()
    }

    protected fun getSecondaryFieldText(): String {
        return innerBinding.textEditSecondary.text.toString()
    }

    protected fun isSwitchChecked(): Boolean {
        return innerBinding.switchSelector.isChecked
    }

    protected fun clearFields() {
        innerBinding.textEditPrimary.text?.clear()
        innerBinding.selector.text = null
        innerBinding.textEditSecondary.text?.clear()
        innerBinding.switchSelector.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _innerBinding = null
    }

}