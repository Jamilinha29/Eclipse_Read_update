2package com.mili.eclipsereads.ui.details

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.mili.eclipsereads.R

class Info_livro : Fragment() {

    private lateinit var bookId: String
    private lateinit var favoriteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookId = arguments?.getString("BOOK_ID") ?: "default_book_id"
        return inflater.inflate(R.layout.activity_info_livro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainView = view.findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        favoriteButton = view.findViewById(R.id.button18)
        val readButton = view.findViewById<Button>(R.id.button19)
        val readNowButton = view.findViewById<Button>(R.id.button17)

        updateFavoriteButtonState()

        favoriteButton.setOnClickListener {
            toggleFavoriteStatus()
        }

        // Lógica para os outros botões permanece a mesma
        val prefs = requireActivity().getSharedPreferences("DADOS_LIVROS", Context.MODE_PRIVATE)

        readButton.setOnClickListener {
            val read = prefs.getStringSet("READ", mutableSetOf()) ?: mutableSetOf()
            if (!read.contains(bookId)) {
                read.add(bookId)
                prefs.edit().putStringSet("READ", read).apply()
                Toast.makeText(requireContext(), "Livro marcado como lido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Você já marcou este livro como lido", Toast.LENGTH_SHORT).show()
            }
        }

        readNowButton.setOnClickListener {
            val reading = prefs.getStringSet("READING", mutableSetOf()) ?: mutableSetOf()
            if (!reading.contains(bookId)) {
                reading.add(bookId)
                prefs.edit().putStringSet("READING", reading).apply()
                Toast.makeText(requireContext(), "Iniciando a leitura...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Você já está lendo este livro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isFavorite(): Boolean {
        val prefs = requireActivity().getSharedPreferences("DADOS_LIVROS", Context.MODE_PRIVATE)
        val favorites = prefs.getStringSet("FAVORITES", emptySet()) ?: emptySet()
        return favorites.contains(bookId)
    }

    private fun toggleFavoriteStatus() {
        val prefs = requireActivity().getSharedPreferences("DADOS_LIVROS", Context.MODE_PRIVATE)
        val favorites = prefs.getStringSet("FAVORITES", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (favorites.contains(bookId)) {
            favorites.remove(bookId)
            Toast.makeText(requireContext(), "Livro removido dos favoritos", Toast.LENGTH_SHORT).show()
        } else {
            favorites.add(bookId)
            Toast.makeText(requireContext(), "Livro adicionado aos favoritos", Toast.LENGTH_SHORT).show()
        }

        prefs.edit().putStringSet("FAVORITES", favorites).apply()
        updateFavoriteButtonState()
    }

    private fun updateFavoriteButtonState() {
        // Pega o drawable da estrela que já existe no botão
        val starDrawable = favoriteButton.compoundDrawables[0] // 0 = drawable da esquerda (start)

        if (isFavorite()) {
            favoriteButton.text = getString(R.string.remover_dos_favoritos)
            // Pinta a estrela de amarelo para indicar que é um favorito
            starDrawable.setTint(Color.YELLOW)
        } else {
            favoriteButton.text = getString(R.string.favoritar)
            // Remove a cor, voltando ao padrão do tema
            starDrawable.setTintList(null)
        }
    }
}