package com.example.avatar_ai_manager.adaptor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_manager.databinding.ListItemBinding

class ClickableListAdaptor<T>(
    private val getColumn1Text: (T) -> String,
    private val getColumn2Text: (T) -> String,
    private val onClickedPrimary: (T) -> Unit,
    private val onClickedSecondary: ((T) -> Unit)?,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, ClickableListAdaptor.ClickableListViewHolder<T>>(diffCallback) {

    companion object {

        fun <T : Any> create(
            getColumn1Text: (T) -> String,
            getColumn2Text: (T) -> String,
            onClickedPrimary: (T) -> Unit,
            onClickedSecondary: ((T) -> Unit)?
        ): ClickableListAdaptor<T> {
            return ClickableListAdaptor(
                getColumn1Text,
                getColumn2Text,
                onClickedPrimary,
                onClickedSecondary,
                getDiffCallBack()
            )
        }

        private fun <T : Any> getDiffCallBack() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem === newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                // Since T is a data class, equals() is implemented by default.
                return oldItem == newItem
            }
        }

    }

    class ClickableListViewHolder<T>(
        private var binding: ListItemBinding,
        private val getColumn1Text: (T) -> String,
        private val getColumn2Text: (T) -> String,
        private val onClickedPrimary: (T) -> Unit,
        private val onClickedSecondary: ((T) -> Unit)?,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: T) {
            binding.column1.text = getColumn1Text(item)
            binding.column2.text = getColumn2Text(item)

            if (onClickedSecondary == null) {
                setSingleClickListener(binding, item)
            } else {
                setDoubleClickListeners(binding, item, onClickedSecondary)
            }
        }

        private fun setSingleClickListener(binding: ListItemBinding, item: T) {
            binding.root.setOnClickListener {
                onClickedPrimary(item)
            }
        }

        private fun setDoubleClickListeners(
            binding: ListItemBinding,
            item: T,
            onClickedSecondary: (T) -> Unit
        ) {
            binding.column1.setOnClickListener {
                onClickedPrimary(item)
            }
            binding.column2.setOnClickListener {
                onClickedSecondary(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListViewHolder<T> {
        return ClickableListViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            getColumn1Text,
            getColumn2Text,
            onClickedPrimary,
            onClickedSecondary
        )
    }

    override fun onBindViewHolder(holder: ClickableListViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
    }

}