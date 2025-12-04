package com.mili.eclipsereads.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.mili.eclipsereads.R
import com.mili.eclipsereads.ui.login.Log0regis
import com.mili.eclipsereads.viewmodel.ProfileUiState
import com.mili.eclipsereads.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Perfil : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var avatarImage: ShapeableImageView
    private lateinit var bannerImage: ImageView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { uri -> profileViewModel.updateProfileImage(uri) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        avatarImage = view.findViewById(R.id.avatar_image)
        bannerImage = view.findViewById(R.id.banner_image)

        avatarImage.setOnClickListener { openGallery() }
        bannerImage.setOnClickListener { openGallery() } // A lógica para diferenciar banner/avatar precisará ser ajustada no ViewModel

        observeUiState()
        observeNavigationEvents()

        view.findViewById<Button>(R.id.button29).setOnClickListener { profileViewModel.signOut() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Success -> {
                            val profile = state.profile
                            view?.findViewById<TextView>(R.id.textView23)?.text = profile.fullName
                            view?.findViewById<TextView>(R.id.textView24)?.text = profile.email
                            Glide.with(this@Perfil).load(profile.avatarUrl).circleCrop().into(avatarImage)

                            // Update stats
                            view?.findViewById<TextView>(R.id.favoritos_count)?.text = state.favoritesCount.toString()
                            view?.findViewById<TextView>(R.id.lendo_count)?.text = state.readingCount.toString()
                            view?.findViewById<TextView>(R.id.dropados_count)?.text = state.droppedCount.toString()
                            view?.findViewById<GridLayout>(R.id.stats_grid)?.isVisible = true
                        }
                        else -> {
                            view?.findViewById<TextView>(R.id.textView23)?.text = "Usuário"
                            view?.findViewById<GridLayout>(R.id.stats_grid)?.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun observeNavigationEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.navigationEvent.collect { 
                    val intent = Intent(requireActivity(), Log0regis::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.refreshProfile()
    }
}