package com.mili.eclipsereads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mili.eclipsereads.R
import com.mili.eclipsereads.domain.models.Books
import com.mili.eclipsereads.ui.details.Info_livro
import com.mili.eclipsereads.viewmodel.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Minha_biblioteca : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var readingRecyclerView: RecyclerView
    private lateinit var favoritesAdapter: SimpleBookAdapter
    private lateinit var readingAdapter: SimpleBookAdapter
    private lateinit var emptyFavorites: TextView
    private lateinit var emptyReading: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_minha_biblioteca, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerViews()
        observeUiState()
    }

    private fun setupViews(view: View) {
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view) // Substitua pelo ID correto
        readingRecyclerView = view.findViewById(R.id.reading_recycler_view) // Substitua pelo ID correto
        emptyFavorites = view.findViewById(R.id.empty_favorites_text) // Substitua pelo ID correto
        emptyReading = view.findViewById(R.id.empty_reading_text) // Substitua pelo ID correto
    }

    private fun setupRecyclerViews() {
        favoritesAdapter = SimpleBookAdapter { navigateToBookDetail(it.id) }
        readingAdapter = SimpleBookAdapter { navigateToBookDetail(it.id) }

        favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        readingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        favoritesRecyclerView.adapter = favoritesAdapter
        readingRecyclerView.adapter = readingAdapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    favoritesAdapter.submitList(state.favorites)
                    readingAdapter.submitList(state.reading)

                    emptyFavorites.isVisible = state.favorites.isEmpty()
                    favoritesRecyclerView.isVisible = state.favorites.isNotEmpty()

                    emptyReading.isVisible = state.reading.isEmpty()
                    readingRecyclerView.isVisible = state.reading.isNotEmpty()
                }
            }
        }
    }

    private fun navigateToBookDetail(bookId: String) {
        val fragment = Info_livro().apply {
            arguments = Bundle().apply { putString("bookId", bookId) }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_central, fragment)
            .addToBackStack(null)
            .commit()
    }
}

// A classe SimpleBookAdapter permanece a mesma
class SimpleBookAdapter(private val onItemClick: (Books) -> Unit) :
    RecyclerView.Adapter<SimpleBookAdapter.BookViewHolder>() {

    private var books: List<Books> = emptyList()

    fun submitList(newBooks: List<Books>) {
        this.books = newBooks
        notifyDataSetChanged() // Para simplificar; use DiffUtil para performance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_simple, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)

        init {
            itemView.setOnClickListener { onItemClick(books[bindingAdapterPosition]) }
        }

        fun bind(book: Books) {
            bookTitle.text = book.title
            Glide.with(itemView.context).load(book.coverUrl).into(bookCover)
        }
    }
}