package com.mili.eclipsereads.ui.login

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.mili.eclipsereads.R

@AndroidEntryPointndridEntryPoint
class Log0regis : AppCompatActivity() {
    private var mostrandoLogin = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log0regis)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botaoCadastro = findViewById<Button>(R.id.button9)
        val botaoLogin = findViewById<Button>(R.id.button3)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                replace<Formulario_login>(R.id.form_placeholder)
            }
        }

        animarBotoes(botaoLogin, botaoCadastro, mostrandoLogin)


        botaoCadastro?.setOnClickListener {
            if (mostrandoLogin) {
                mostrandoLogin = false
                animarBotoes(botaoLogin, botaoCadastro, false)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    setCustomAnimations(
                        R.anim.slide_in_right,   // entra da direita
                        R.anim.slide_out_left    // sai para esquerda
                    )
                    replace<Formulario_registro>(R.id.form_placeholder)
                }
            }
        }

        botaoLogin?.setOnClickListener {
            if (!mostrandoLogin) {
                mostrandoLogin = true
                animarBotoes(botaoLogin, botaoCadastro, true)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    setCustomAnimations(
                        R.anim.slide_in_left,    // entra da esquerda
                        R.anim.slide_out_right   // sai para direita
                    )
                    replace<Formulario_login>(R.id.form_placeholder)
                }
            }
        }
    }
    private fun animarBotoes(botaoLogin: Button, botaoCadastro: Button, mostrandoLogin: Boolean) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.fade_tint)

        if (mostrandoLogin) {
            // LOGIN ativo
            botaoLogin.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
            botaoLogin.setTextColor(Color.parseColor("#C592FF"))
            botaoLogin.startAnimation(anim)

            // CADASTRO inativo
            botaoCadastro.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#10070707"))
            botaoCadastro.setTextColor(Color.parseColor("#C592FF"))

        } else {
            botaoCadastro.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
            botaoCadastro.setTextColor(Color.parseColor("#C592FF"))
            botaoCadastro.startAnimation(anim)

            botaoLogin.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#10070707"))
            botaoLogin.setTextColor(Color.parseColor("#C592FF"))
        }
    }
}