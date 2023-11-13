package com.sina.mvvm.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sina.mvvm.R
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.databinding.ItemNoteBinding

class HomeAdapter(
    private val onItemClick: (Note) -> Unit,
    private val onFavoriteClick: (Note, Boolean) -> Unit
) :
    ListAdapter<Note, HomeAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Note?>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }) {
    inner class ViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick.invoke(getItem(adapterPosition))
            }
        }
        fun bind(item: Note) {
            with(binding) {
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvDate.text = StringBuilder().append("Created in:").append(item.createDate)
                imgFavorite.setImageResource(if (item.isFavorite) R.drawable.ic_favorite_fill else R.drawable.ic_favorite_empty)
                imgFavorite.setOnClickListener {
                    onFavoriteClick.invoke(getItem(adapterPosition),!item.isFavorite)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}