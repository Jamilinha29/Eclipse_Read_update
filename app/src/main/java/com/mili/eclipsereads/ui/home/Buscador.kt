package com.mili.eclipsereads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mili.eclipsereads.R
import com.mili.eclipsereads.domain.models.Books
import com.mili.eclipsereads.ui.details.Info_livro
import com.mili.eclipsereads.viewmodel.BooksUiState
import com.mili.eclipsereads.viewmodel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Buscador : Fragment() {

    private val booksViewModel: BooksViewModel by viewModels()

    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var mostReadRecyclerView: RecyclerView
    private lateinit var newAdditionsRecyclerView: RecyclerView

    private lateinit var recommendedAdapter: BookAdapter
    private lateinit var mostReadAdapter: BookAdapter
    private lateinit var newAdditionsAdapter: BookAdapter

    private var allBooks: List<Books> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_buscador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets(view)
        setupRecyclerViews(view)
        setupSearchView(view)
        observeBooksState()
    }

    private fun setupWindowInsets(view: View) {
        val mainView = view.findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun setupRecyclerViews(view: View) {
        recommendedRecyclerView = view.findViewById(R.id.recommended_recycler_view)
        mostReadRecyclerView = view.findViewById(R.id.most_read_recycler_view)
        newAdditionsRecyclerView = view.findViewById(R.id.new_additions_recycler_view)

        recommendedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mostReadRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        newAdditionsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recommendedAdapter = BookAdapter { navigateToBookDetail(it.id) }
        mostReadAdapter = BookAdapter { navigateToBookDetail(it.id) }
        newAdditionsAdapter = BookAdapter { navigateToBookDetail(it.id) }

        recommendedRecyclerView.adapter = recommendedAdapter
        mostReadRecyclerView.adapter = mostReadAdapter
        newAdditionsRecyclerView.adapter = newAdditionsAdapter
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.search_view_no_layout)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterResults(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterResults(newText)
                return true
            }
        })
    }

    private fun observeBooksState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                booksViewModel.uiState.collect { state ->
                    when (state) {
                        is BooksUiState.Success -> {
                            allBooks = state.books
                            // TODO: Implementar lógica de separação para cada categoria
                            recommendedAdapter.updateBooks(allBooks)
                            mostReadAdapter.updateBooks(allBooks)
                            newAdditionsAdapter.updateBooks(allBooks)
                        }
                        is BooksUiState.Error -> {
                            // TODO: Mostrar erro na UI
                        }
                        is BooksUiState.Loading -> {
                            // TODO: Mostrar loading na UI
                        }
                    }
                }
            }
        }
    }

    private fun filterResults(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            allBooks
        } else {
            allBooks.filter { it.title.contains(query, ignoreCase = true) }
        }
        // Por agora, atualizando todos os adaptadores com a lista filtrada.
        recommendedAdapter.updateBooks(filteredList)
        mostReadAdapter.updateBooks(filteredList)
        newAdditionsAdapter.updateBooks(filteredList)
    }

    private fun navigateToBookDetail(bookId: Int) {
        val fragment = Info_livro().apply {
            arguments = Bundle().apply {
                putInt("bookId", bookId)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_central, fragment)
            .addToBackStack(null)
            .commit()
    }

    class BookAdapter(private val onItemClick: (Books) -> Unit) :
        RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

        private var books: List<Books> = emptyList()

        fun updateBooks(newBooks: List<Books>) {
            this.books = newBooks
            notifyDataSetChanged()
        }

        class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // TODO: Referenciar as views do item do livro
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_cover, parent, false)
            return BookViewHolder(view)
        }

        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            val book = books[position]
            // TODO: Preencher os dados do livro no ViewHolder (Glide para a capa, etc.)

            holder.itemView.setOnClickListener {
                onItemClick(book)
            }
        }

        override fun getItemCount() = books.size
    }
}