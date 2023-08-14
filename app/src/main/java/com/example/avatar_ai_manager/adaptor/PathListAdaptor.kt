package com.example.avatar_ai_manager.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avatar_ai_manager.databinding.ListItemBinding
import com.example.avatar_ai_cloud_storage.database.entity.Path

class PathListAdaptor(private val onPathClicked: (String, String, Int) -> Unit) :
    ListAdapter<Path, PathListAdaptor.PathListViewHolder>(
        DiffCallback
    ) {

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<Path>() {
                override fun areItemsTheSame(
                    oldItem: Path,
                    newItem: Path
                ): Boolean {
                    return oldItem.anchor2 == newItem.anchor2
                }

                override fun areContentsTheSame(
                    oldItem: Path,
                    newItem: Path
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    class PathListViewHolder(
        private var binding: ListItemBinding,
        private val onPathClicked: (String, String, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(path: Path) {
            binding.column1.text = path.anchor2
            binding.column2.text = path.distance.toString()

            binding.root.setOnClickListener {
                onPathClicked(path.anchor1, path.anchor2, path.distance)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathListViewHolder {
        return PathListViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onPathClicked
        )
    }

    override fun onBindViewHolder(holder: PathListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}