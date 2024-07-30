package com.yogaap.onlineshop.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.yogaap.onlineshop.Helper.ImageHelper
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.ViewModel.ProfileViewModel
import com.yogaap.onlineshop.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: ProfileViewModel

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        sessionManager = SessionManager(this)

        binding.profileBack.setOnClickListener {
            finish()
        }

        binding.profileSetting.setOnClickListener {
            // TODO: Add setting activity
        }

        binding.profileLogout.setOnClickListener {
            logout()
        }

        binding.profileImage.setOnClickListener {
            showImageOptionsDialog()
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data
            val compressedImage = ImageHelper.compressImage(this, imageUri!!)
            viewModel.uploadProfileImage(compressedImage)
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this, { user ->
            binding.profileName.text = user?.name
            binding.profileEmail.text = user?.email
            val imageUrl = user?.imageUrl ?: sessionManager.getImageUrl()
            if (imageUrl != null) {
                Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.img_profile)
            }
        })

        viewModel.imageUploadResult.observe(this, { result ->
            result?.let {
                if (it.first != null) {
                    Glide.with(this)
                        .load(it.first)
                        .into(binding.profileImage)
                }
            }
        })

        viewModel.imageDeleteResult.observe(this, { success ->
            if (success == true) {
                binding.profileImage.setImageResource(R.drawable.img_profile)
            }
        })
    }

    private fun logout() {
        auth.signOut()
        sessionManager.clearUserSession()
        sessionManager.clearImageUrl()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("Lihat Gambar", "Edit Gambar", "Hapus Gambar")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> viewProfileImage()
                1 -> ImageHelper.openImageChooser(this)
                2 -> viewModel.deleteProfileImage()
            }
        }
        builder.show()
    }

    private fun viewProfileImage() {
        viewModel.viewProfileImage(this)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}