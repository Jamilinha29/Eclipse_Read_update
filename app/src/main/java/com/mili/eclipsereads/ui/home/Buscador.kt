package com.mili.eclipsereads.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mili.eclipsereads.R
import com.mili.eclipsereads.domain.models.Books
import com.mili.eclipsereads.ui.details.Info_livro
import com.mili.eclipsereads.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Buscador : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchResultAdapter
    private lateinit var loadingView: View
    private lateinit var emptyResultsView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_buscador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView()
        setupSearchView(view)
        observeUiState()
    }

    private fun setupViews(view: View) {
        searchRecyclerView = view.findViewById(R.id.search_results_recycler_view)
        loadingView = view.findViewById(R.id.search_loading_view)
        emptyResultsView = view.findViewById(R.id.search_empty_view)
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultAdapter { navigateToBookDetail(it.id) }
        searchRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = searchAdapter
        }
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.search_view_no_layout)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearchQueryChanged(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingView.isVisible = state.isLoading
                    searchAdapter.submitList(state.results)

                    val showEmptyView = !state.isLoading && state.results.isEmpty() && !state.isInitial
                    emptyResultsView.isVisible = showEmptyView
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

class SearchResultAdapter(private val onItemClick: (Books) -> Unit) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var items: List<Books> = emptyList()

    fun submitList(newItems: List<Books>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_cover, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover_image)

        init {
            itemView.setOnClickListener {
                onItemClick(items[bindingAdapterPosition])
            }
        }

        fun bind(book: Books) {
            Glide.with(itemView.context).load(book.coverUrl).into(bookCover)
        }
    }
}
