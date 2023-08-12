package com.example.avatar_ai_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_cloud_storage.database.entity.Anchor
import com.example.avatar_ai_manager.databinding.ListItemBinding

class AnchorListAdaptor(
    private val onAnchorClicked: (String) -> Unit,
    private val onDescriptionClicked: (String, String) -> Unit
) :
    ListAdapter<Anchor, AnchorListAdaptor.AnchorListViewHolder>(
        DiffCallback
    ) {

    companion object {
        private val DiffCallback =
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

    class AnchorListViewHolder(
        private var binding: ListItemBinding,
        private val onAnchorClicked: (String) -> Unit,
        private val onDescriptionClicked: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anchor: Anchor) {
            binding.column1.text = anchor.id
            binding.column2.text = anchor.description

            binding.column1.setOnClickListener {
                onAnchorClicked(anchor.id)
            }
            binding.column2.setOnClickListener {
                onDescriptionClicked(anchor.id, anchor.description)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnchorListViewHolder {
        return AnchorListViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onAnchorClicked,
            onDescriptionClicked
        )
    }

    override fun onBindViewHolder(holder: AnchorListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}