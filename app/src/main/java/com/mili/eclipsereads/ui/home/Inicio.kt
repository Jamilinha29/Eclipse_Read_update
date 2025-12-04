package com.mili.eclipsereads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mili.eclipsereads.R
import com.mili.eclipsereads.ui.details.Info_livro
import com.mili.eclipsereads.viewmodel.BooksViewModel
import com.mili.eclipsereads.viewmodel.UserUiState
import com.mili.eclipsereads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Inicio : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private val booksViewModel: BooksViewModel by viewModels()

    private lateinit var continueReadingRecyclerView: RecyclerView
    private lateinit var dailyUpdatesRecyclerView: RecyclerView
    private lateinit var emptyContinueReading: TextView
    private lateinit var welcomeTextView: TextView
    private lateinit var bookPagingAdapter: BookPagingAdapter

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeUserState()
        observePagingData()

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

    private fun setupRecyclerViews() {
        bookPagingAdapter = BookPagingAdapter { book ->
            navigateToBookDetail(book.id)
        }

        dailyUpdatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bookPagingAdapter
        }

        // Temporarily hide the continue reading section
        continueReadingRecyclerView.visibility = View.GONE
        emptyContinueReading.visibility = View.GONE
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
                        is UserUi-State.Loading -> {
                            welcomeTextView.text = "Carregando..."
                        }
                    }
                }
            }
        }
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                booksViewModel.books.collectLatest { pagingData ->
                    bookPagingAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun navigateToBookDetail(bookId: String) {
        val fragment = Info_livro().apply {
            arguments = Bundle().apply {
                putString("bookId", bookId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_central, fragment)
            .addToBackStack(null)
            .commit()
    }
}