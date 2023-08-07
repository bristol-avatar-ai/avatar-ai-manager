package com.example.ai_avatar_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_avatar_manager.databinding.ListItemBinding
import com.example.avatar_ai_cloud_storage.database.Exhibition

class ExhibitionListAdaptor(private val onExhibitionClicked: (String, String) -> Unit) :
    ListAdapter<Exhibition, ExhibitionListAdaptor.ExhibitionListViewHolder>(
        DiffCallback
    ) {

    companion object {
        private val DiffCallback = object :
            DiffUtil.ItemCallback<Exhibition>() {
            override fun areItemsTheSame(
                oldItem: Exhibition,
                newItem: Exhibition
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Exhibition,
                newItem: Exhibition
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ExhibitionListViewHolder(
        private var binding: ListItemBinding,
        private val onExhibitionClicked: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exhibition: Exhibition) {
            binding.column1.text = exhibition.name
            binding.column2.text = exhibition.description

            binding.root.setOnClickListener {
                onExhibitionClicked(exhibition.name, exhibition.description)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExhibitionListViewHolder {
        return ExhibitionListViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onExhibitionClicked
        )
    }

    override fun onBindViewHolder(holder: ExhibitionListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}