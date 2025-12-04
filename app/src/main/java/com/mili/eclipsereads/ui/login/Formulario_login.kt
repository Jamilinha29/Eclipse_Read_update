package com.mili.eclipsereads.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.mili.eclipsereads.R
import com.mili.eclipsereads.ui.home.Central
import com.mili.eclipsereads.viewmodel.LoginUiState
import com.mili.eclipsereads.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class Formulario_login : Fragment() {

    private val PREFS_NAME = "com.mili.eclipsereads.prefs"
    private val PREF_EMAIL = "email"
    private val PREF_REMEMBER = "remember"

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    account.idToken?.let { idToken ->
                        loginViewModel.signInWithGoogle(idToken)
                    }
                } catch (e: ApiException) {
                    Timber.w(e, "Google sign in failed")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_formulario_login, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.Email00)
        val senhaEditText = view.findViewById<EditText>(R.id.senha00)
        val entrarButton = view.findViewById<Button>(R.id.button2)
        val rememberMeCheckBox = view.findViewById<CheckBox>(R.id.checkBox)
        val signInButton = view.findViewById<SignInButton>(R.id.sign_in_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val sharedPreferences =
            requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val shouldRemember = sharedPreferences.getBoolean(PREF_REMEMBER, false)

        if (shouldRemember) {
            emailEditText.setText(sharedPreferences.getString(PREF_EMAIL, ""))
            rememberMeCheckBox.isChecked = true
        }

        entrarButton.isEnabled =
            emailEditText.text.isNotEmpty() && senhaEditText.text.isNotEmpty()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                entrarButton.isEnabled =
                    emailEditText.text.isNotEmpty() && senhaEditText.text.isNotEmpty()
            }
        }

        emailEditText.addTextChangedListener(textWatcher)
        senhaEditText.addTextChangedListener(textWatcher)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collectLatest { state ->
                    when (state) {
                        is LoginUiState.Idle -> {
                            entrarButton.isEnabled = true
                            signInButton.isEnabled = true
                        }
                        is LoginUiState.Loading -> {
                            entrarButton.isEnabled = false
                            signInButton.isEnabled = false
                        }
                        is LoginUiState.Success -> {
                            val editor = sharedPreferences.edit()
                            if (rememberMeCheckBox.isChecked) {
                                editor.putString(PREF_EMAIL, emailEditText.text.toString())
                                editor.putBoolean(PREF_REMEMBER, true)
                            } else {
                                editor.remove(PREF_EMAIL)
                                editor.putBoolean(PREF_REMEMBER, false)
                            }
                            editor.apply()

                            val intent = Intent(requireContext(), Central::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        is LoginUiState.Error -> {
                            entrarButton.isEnabled = true
                            signInButton.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                state.exception.message ?: "Erro ao autenticar",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        entrarButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val senha = senhaEditText.text.toString().trim()
            loginViewModel.signInWithEmail(email, senha)
        }

        signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        return view
    }
}