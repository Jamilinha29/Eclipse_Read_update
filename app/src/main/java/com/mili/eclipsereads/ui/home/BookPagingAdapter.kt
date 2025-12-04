package com.mili.eclipsereads.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mili.eclipsereads.R
import com.mili.eclipsereads.domain.models.Books

class BookPagingAdapter(private val onItemClick: (Books) -> Unit) : PagingDataAdapter<Books, BookPagingAdapter.BookViewHolder>(BOOK_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_simple, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        if (book != null) {
            holder.bind(book)
        }
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)

        init {
            itemView.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { book -> onItemClick(book) }
            }
        }

        fun bind(book: Books) {
            bookTitle.text = book.title
            Glide.with(itemView.context)
                .load(book.coverUrl)
                .placeholder(R.drawable.placeholder_book_cover) // Imagem de placeholder
                .into(bookCover)
        }
    }

    companion object {
        private val BOOK_COMPARATOR = object : DiffUtil.ItemCallback<Books>() {
            override fun areItemsTheSame(oldItem: Books, newItem: Books): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Books, newItem: Books): Boolean =
                oldItem == newItem
        }
    }
}