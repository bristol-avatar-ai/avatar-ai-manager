package com.example.ai_avatar_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_avatar_manager.database.Path
import com.example.ai_avatar_manager.databinding.ListItemBinding

class PathListAdaptor :
    ListAdapter<Path, PathListAdaptor.PathListViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Path>() {
            override fun areItemsTheSame(oldItem: Path, newItem: Path): Boolean {
                return oldItem.destination == newItem.destination
            }

            override fun areContentsTheSame(oldItem: Path, newItem: Path): Boolean {
                return oldItem == newItem
            }
        }
    }

    class PathListViewHolder(
        private var binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(path: Path) {
            binding.column1.text = path.destination
            binding.column2.text = path.distance.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathListViewHolder {
        return PathListViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PathListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}