package com.mili.eclipsereads.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.mili.eclipsereads.R
import com.mili.eclipsereads.viewmodel.ProfileUiState
import com.mili.eclipsereads.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Configuracoes : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_configuracoes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets(view)
        setupNameChange(view)
        setupThemeButtons(view)
        setupInterfaceAndData(view)
        observeProfileState()
    }

    private fun setupWindowInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNameChange(view: View) {
        val nomeUsuarioEditText = view.findViewById<TextInputEditText>(R.id.nome_usuario_edittext)
        val salvarNomeButton = view.findViewById<Button>(R.id.button_salvar_nome)

        salvarNomeButton.setOnClickListener {
            val novoNome = nomeUsuarioEditText.text.toString()
            if (novoNome.isNotBlank()) {
                profileViewModel.updateProfile(novoNome)
                Toast.makeText(requireContext(), "Nome salvo com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "O nome não pode estar em branco", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeProfileState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    if (state is ProfileUiState.Success) {
                        view?.findViewById<TextInputEditText>(R.id.nome_usuario_edittext)?.setText(state.profile.fullName)
                    }
                }
            }
        }
    }

    private fun setupThemeButtons(view: View) {
        val btnTemaClaro = view.findViewById<Button>(R.id.button30)
        val btnTemaEscuro = view.findViewById<Button>(R.id.button32)

        btnTemaClaro.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            requireActivity().recreate()
        }

        btnTemaEscuro.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            requireActivity().recreate()
        }
    }

    private fun setupInterfaceAndData(view: View) {
        val switchNotificacoes = view.findViewById<SwitchMaterial>(R.id.switch_notificacoes)
        val switchSincronizacao = view.findViewById<SwitchMaterial>(R.id.switch_sincronizacao)
        val btnExportar = view.findViewById<Button>(R.id.button_exportar)
        val btnLimpar = view.findViewById<Button>(R.id.button_limpar)

        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), "Notificações ${if (isChecked) "ativadas" else "desativadas"}", Toast.LENGTH_SHORT).show()
        }

        switchSincronizacao.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), "Sincronização ${if (isChecked) "ativada" else "desativada"}", Toast.LENGTH_SHORT).show()
        }

        btnExportar.setOnClickListener {
            Toast.makeText(requireContext(), "Exportando dados...", Toast.LENGTH_SHORT).show()
        }

        btnLimpar.setOnClickListener {
            showClearCacheConfirmationDialog()
        }
    }

    private fun showClearCacheConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Limpar Cache")
            .setMessage("Tem certeza de que deseja limpar o cache do aplicativo?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Limpar") { _, _ -> clearCache() }
            .show()
    }

    private fun clearCache() {
        try {
            val cacheDir = requireContext().cacheDir
            if (cacheDir != null && cacheDir.isDirectory) {
                deleteDir(cacheDir)
                Toast.makeText(requireContext(), "Cache limpo com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Falha ao limpar o cache", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Falha ao limpar o cache", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            dir.list()?.forEach { child ->
                if (!deleteDir(File(dir, child))) return false
            }
        }
        return dir?.delete() ?: false
    }
}