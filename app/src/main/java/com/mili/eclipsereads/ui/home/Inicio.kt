package com.mili.eclipsereads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import com.mili.eclipsereads.viewmodel.UserUiState
import com.mili.eclipsereads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Inicio : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private val booksViewModel: BooksViewModel by viewModels()

    private lateinit var continueReadingRecyclerView: RecyclerView
    private lateinit var dailyUpdatesRecyclerView: RecyclerView
    private lateinit var emptyContinueReading: TextView
    private lateinit var emptyDailyUpdates: TextView
    private lateinit var welcomeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_inicio, container, false)
        welcomeTextView = view.findViewById(R.id.textView7)
        continueReadingRecyclerView = view.findViewById(R.id.continue_reading_recycler_view)
        dailyUpdatesRecyclerView = view.findViewById(R.id.daily_updates_recycler_view)
        emptyContinueReading = view.findViewById(R.id.empty_continue_reading)
        emptyDailyUpdates = view.findViewById(R.id.empty_daily_updates)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainView = view.findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        observeUserState()
        observeBooksState()

        val verMaisButton1 = view.findViewById<Button>(R.id.button100)
        val verMaisButton2 = view.findViewById<Button>(R.id.button10)

        val navigateToBuscador = View.OnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_central, Buscador())
                .addToBackStack(null)
                .commit()
        }

        verMaisButton1.setOnClickListener(navigateToBuscador)
        verMaisButton2.setOnClickListener(navigateToBuscador)
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.uiState.collect { state ->
                    when (state) {
                        is UserUiState.Success -> {
                            val user = state.user
                            val displayName = user.userMetadata?.get("full_name")?.toString()
                                ?: user.email
                                ?: "Usuário"
                            welcomeTextView.text = "Olá, $displayName!"
                        }
                        is UserUiState.Error -> {
                            welcomeTextView.text = "Olá, Usuário!"
                        }
                        is UserUiState.Loading -> {
                            welcomeTextView.text = "Carregando..."
                        }
                    }
                }
            }
        }
    }

    private fun observeBooksState() {
        continueReadingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dailyUpdatesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                booksViewModel.uiState.collect { state ->
                    when (state) {
                        is BooksUiState.Success -> {
                            val allBooks = state.books
                            // TODO: Implementar a lógica para separar os livros em continueReading e dailyUpdates
                            val continueReadingBooks = emptyList<Books>()
                            val dailyUpdatesBooks = allBooks

                            updateRecyclerView(continueReadingRecyclerView, emptyContinueReading, continueReadingBooks)
                            updateRecyclerView(dailyUpdatesRecyclerView, emptyDailyUpdates, dailyUpdatesBooks)
                        }
                        is BooksUiState.Loading -> {
                            emptyContinueReading.text = "Carregando..."
                            emptyDailyUpdates.text = "Carregando..."
                        }
                        is BooksUiState.Error -> {
                            emptyContinueReading.text = "Erro ao carregar livros"
                            emptyDailyUpdates.text = "Erro ao carregar livros"
                        }
                    }
                }
            }
        }
    }

    private fun updateRecyclerView(recyclerView: RecyclerView, emptyView: TextView, books: List<Books>) {
        val adapter = BookAdapter(books) { book ->
            navigateToBookDetail(book.id)
        }
        if (books.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
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

    class BookAdapter(private val books: List<Books>, private val onItemClick: (Books) -> Unit) :
        RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

        class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // Referências para as views do item do livro
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_simple, parent, false)
            return BookViewHolder(view)
        }

        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
            val book = books[position]
            holder.itemView.setOnClickListener {
                onItemClick(book)
            }
        }

        override fun getItemCount() = books.size
    }
}