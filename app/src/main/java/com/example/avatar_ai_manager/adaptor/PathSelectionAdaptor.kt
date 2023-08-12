package com.example.avatar_ai_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.databinding.ListItemBinding

class PathSelectionAdaptor(
    private val originId: String,
    private val onAnchorClicked: (String, String) -> Unit
) :
    ListAdapter<Anchor, PathSelectionAdaptor.PathSelectionAdaptorViewHolder>(
        DiffCallback
    ) {

    companion object {
        private val DiffCallback by lazy {
            object : DiffUtil.ItemCallback<Anchor>() {
                override fun areItemsTheSame(
                    oldItem: Anchor,
                    newItem: Anchor
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Anchor,
                    newItem: Anchor
                ): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }

    class PathSelectionAdaptorViewHolder(
        private var binding: ListItemBinding,
        private val originId: String,
        private val onAnchorClicked: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anchor: Anchor) {
            binding.column1.text = anchor.id
            binding.column2.text = anchor.description

            binding.root.setOnClickListener {
                onAnchorClicked(originId, anchor.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PathSelectionAdaptorViewHolder {
        return PathSelectionAdaptorViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            originId,
            onAnchorClicked
        )
    }

    override fun onBindViewHolder(holder: PathSelectionAdaptorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}