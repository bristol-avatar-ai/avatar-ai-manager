package com.example.avatar_ai_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_cloud_storage.database.entity.Feature
import com.example.avatar_ai_manager.databinding.ListItemBinding

class ExhibitionListAdaptor(private val onExhibitionClicked: (String, String) -> Unit) :
    ListAdapter<Feature, ExhibitionListAdaptor.ExhibitionListViewHolder>(
        DiffCallback
    ) {

    companion object {
        private val DiffCallback = object :
            DiffUtil.ItemCallback<Feature>() {
            override fun areItemsTheSame(
                oldItem: Feature,
                newItem: Feature
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Feature,
                newItem: Feature
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ExhibitionListViewHolder(
        private var binding: ListItemBinding,
        private val onExhibitionClicked: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.column1.text = feature.name
            binding.column2.text = feature.description

            binding.root.setOnClickListener {
                onExhibitionClicked(feature.name, feature.description)
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