package com.mili.eclipsereads.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mili.eclipsereads.R
import com.mili.eclipsereads.ui.home.Central
import com.mili.eclipsereads.viewmodel.RegisterUiState
import com.mili.eclipsereads.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Formulario_registro : Fragment() {

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_formulario_registro, container, false)

        val nomeEditText = view.findViewById<EditText>(R.id.Nome)
        val emailEditText = view.findViewById<EditText>(R.id.Email)
        val senhaEditText = view.findViewById<EditText>(R.id.Senha)
        val confirmarSenhaEditText = view.findViewById<EditText>(R.id.Confirmarsenha)
        val registerButton = view.findViewById<Button>(R.id.button4)

        registerButton.isEnabled = false

        val formTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val nomeInput = nomeEditText.text.toString().trim()
                val emailInput = emailEditText.text.toString().trim()
                val senhaInput = senhaEditText.text.toString().trim()
                val confirmarSenhaInput = confirmarSenhaEditText.text.toString().trim()

                val camposPreenchidos =
                    nomeInput.isNotEmpty() &&
                            emailInput.isNotEmpty() &&
                            senhaInput.isNotEmpty() &&
                            confirmarSenhaInput.isNotEmpty()

                val senhasCoincidem = senhaInput == confirmarSenhaInput

                registerButton.isEnabled = camposPreenchidos && senhasCoincidem

                if (camposPreenchidos && !senhasCoincidem) {
                    confirmarSenhaEditText.error = "As senhas não coincidem"
                } else {
                    confirmarSenhaEditText.error = null
                }
            }
        }

        nomeEditText.addTextChangedListener(formTextWatcher)
        emailEditText.addTextChangedListener(formTextWatcher)
        senhaEditText.addTextChangedListener(formTextWatcher)
        confirmarSenhaEditText.addTextChangedListener(formTextWatcher)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.uiState.collect { state ->
                    when (state) {
                        is RegisterUiState.Idle -> {
                            registerButton.isEnabled = true
                        }
                        is RegisterUiState.Loading -> {
                            registerButton.isEnabled = false
                        }
                        is RegisterUiState.Success -> {
                            val intent = Intent(requireContext(), Central::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        is RegisterUiState.Error -> {
                            registerButton.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                state.exception.message ?: "Erro ao registrar",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = senhaEditText.text.toString().trim()
            registerViewModel.register(email, password)
        }

        return view
    }
}