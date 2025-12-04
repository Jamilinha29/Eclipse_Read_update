package com.mili.eclipsereads.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.mili.eclipsereads.R
import com.mili.eclipsereads.viewmodel.BookDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Info_livro : Fragment() {

    private val viewModel: BookDetailsViewModel by viewModels()

    private lateinit var favoriteButton: Button
    private lateinit var bookTitle: TextView
    private lateinit var bookAuthor: TextView
    private lateinit var bookDescription: TextView
    private lateinit var bookCover: ImageView
    private lateinit var loadingView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_info_livro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        favoriteButton = view.findViewById(R.id.button18)
        bookTitle = view.findViewById(R.id.book_title_detail)
        bookAuthor = view.findViewById(R.id.book_author_detail)
        bookDescription = view.findViewById(R.id.book_description_detail)
        bookCover = view.findViewById(R.id.book_cover_detail)
        loadingView = view.findViewById(R.id.loading_view)

        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    loadingView.isVisible = state.isLoading

                    if (state.error != null) {
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    }

                    state.book?.let {
                        bookTitle.text = it.title
                        bookAuthor.text = it.author
                        bookDescription.text = it.description ?: ""
                        Glide.with(this@Info_livro).load(it.coverUrl).into(bookCover)
                    }
                    
                    updateFavoriteButtonState(state.isFavorite)
                }
            }
        }
    }

    private fun updateFavoriteButtonState(isFavorite: Boolean) {
        val starDrawable = favoriteButton.compoundDrawables[0]
        if (isFavorite) {
            favoriteButton.text = getString(R.string.remover_dos_favoritos)
            starDrawable.setTint(Color.YELLOW)
        } else {
            favoriteButton.text = getString(R.string.favoritar)
            starDrawable.setTintList(null)
        }
    }
}