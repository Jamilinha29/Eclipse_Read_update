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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.mili.eclipsereads.R
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.ui.login.Formulario_login
import com.mili.eclipsereads.viewmodel.ProfileUiState
import com.mili.eclipsereads.viewmodel.ProfileViewModel
import com.mili.eclipsereads.viewmodel.UserUiState
import com.mili.eclipsereads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Perfil : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var avatarImage: ShapeableImageView
    private lateinit var bannerImage: ImageView
    private var isBannerSelected: Boolean = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { saveAndLoadImage(it) }
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

        avatarImage.setOnClickListener { isBannerSelected = false; openGallery() }
        bannerImage.setOnClickListener { isBannerSelected = true; openGallery() }

        observeUserState()
        observeProfileState()

        view.findViewById<Button>(R.id.button29).setOnClickListener { signOut() }
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.uiState.collect { state ->
                    if (state is UserUiState.Success) {
                        view?.findViewById<TextView>(R.id.textView24)?.text = state.user.email ?: ""
                    }
                }
            }
        }
    }

    private fun observeProfileState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Success -> {
                            val profile = state.profile
                            view?.findViewById<TextView>(R.id.textView23)?.text = profile.fullName
                            // TODO: Carregar estatísticas e imagens do perfil
                            view?.findViewById<GridLayout>(R.id.stats_grid)?.visibility = View.GONE
                        }
                        else -> {
                            // NoProfile, Loading, ou Error
                            view?.findViewById<TextView>(R.id.textView23)?.text = "Usuário"
                            view?.findViewById<GridLayout>(R.id.stats_grid)?.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            authRepository.signOut()
            val intent = Intent(requireActivity(), Formulario_login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun saveAndLoadImage(uri: Uri) {
        // TODO: Mover a lógica de upload para o ViewModel e Repository
        val targetImageView = if (isBannerSelected) bannerImage else avatarImage
        Glide.with(this).load(uri).into(targetImageView)
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.refreshProfile()
    }
}