package com.mili.eclipsereads.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mili.eclipsereads.ui.home.Buscador
import com.mili.eclipsereads.ui.home.Configuracoes
import com.mili.eclipsereads.ui.home.Inicio
import com.mili.eclipsereads.ui.home.Minha_biblioteca
import com.mili.eclipsereads.ui.home.Perfil
import com.mili.eclipsereads.R

class Central : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_central)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            navigateToFragment(Inicio(), addToBackStack = false)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> navigateToFragment(Inicio(), addToBackStack = false)
                R.id.navigation_search -> navigateToFragment(Buscador())
                R.id.navigation_library -> navigateToFragment(Minha_biblioteca())
                R.id.navigation_config -> navigateToFragment(Configuracoes())
                R.id.navigation_profile -> navigateToFragment(Perfil())
            }
            true
        }
        updateProfileIcon()
    }

    private fun navigateToFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_central, fragment)
            if (addToBackStack) {
                addToBackStack(null)
            }
            setReorderingAllowed(true)
            commit()
        }
    }

    private fun updateProfileIcon() {
        // TODO: Substitua esta string de URI de placeholder pela lógica para obter o URI do avatar do usuário
        val avatarUriString = "content://path/to/user/avatar.jpg"
        val avatarUri = Uri.parse(avatarUriString)

        val profileMenuItem: MenuItem = bottomNavigationView.menu.findItem(R.id.navigation_profile)

        Glide.with(this)
            .asBitmap()
            .load(avatarUri)
            .circleCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    profileMenuItem.icon = BitmapDrawable(resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Limpar, se necessário
                }
            })
    }
}