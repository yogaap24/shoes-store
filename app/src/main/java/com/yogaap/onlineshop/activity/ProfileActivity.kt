package com.yogaap.onlineshop.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        binding.profileBack.setOnClickListener {
            finish()
        }

        binding.profileSetting.setOnClickListener {
            // TODO: Add setting activity
        }

        binding.profileLogout.setOnClickListener {
            auth.signOut()
            sessionManager.clearUserSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.profileImage.setOnClickListener {
            showImageOptionsDialog()
        }

        userProfile()
    }

    private fun userProfile() {
        val user = sessionManager.getUserSession()
        user?.let {
            binding.profileName.text = it.name
            binding.profileEmail.text = it.email

            val cachedImageUrl = sessionManager.getImageUrl()
            if (cachedImageUrl != null) {
                Glide.with(this)
                    .load(cachedImageUrl)
                    .placeholder(R.drawable.img_profile)
                    .into(binding.profileImage)
            } else {
                val userUid = auth.currentUser?.uid ?: return
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(userUid)
                databaseReference.child("imageUrl").get()
                    .addOnSuccessListener { snapshot ->
                        val imageUrl = snapshot.getValue(String::class.java)
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .into(binding.profileImage)
                            sessionManager.saveImageUrl(imageUrl)
                        } else {
                            binding.profileImage.setImageResource(R.drawable.img_profile)
                        }
                    }.addOnFailureListener {
                        binding.profileImage.setImageResource(R.drawable.img_profile)
                    }
            }
        }
    }

    private fun showImageOptionsDialog() {
        val options = arrayOf("Lihat Gambar", "Edit Gambar", "Hapus Gambar")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> viewProfileImage()
                1 -> openFileChooser()
                2 -> deleteProfileImage()
            }
        }
        builder.show()
    }

    private fun viewProfileImage() {
        val userUid = auth.currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userUid)
        databaseReference.child("imageUrl").get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.getValue(String::class.java)
            if (!imageUrl.isNullOrEmpty()) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putExtra("imageUrl", imageUrl)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this, "Gambar profil tidak tersedia.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun deleteProfileImage() {
        val user = auth.currentUser
        val userUid = user?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(userUid)

        databaseReference.child("imageUrl").get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.getValue(String::class.java)
            if (!imageUrl.isNullOrEmpty()) {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                storageReference.delete().addOnSuccessListener {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(null)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.profileImage.setImageResource(R.drawable.img_profile)
                                sessionManager.saveImageUrl("")
                                saveProfileImageUriToDatabase(null)
                            }
                        }
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal hapus gambar.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(null)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            binding.profileImage.setImageResource(R.drawable.img_profile)
                            sessionManager.saveImageUrl("")
                            saveProfileImageUriToDatabase(null)
                        }
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gambar tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val fileReference = FirebaseStorage.getInstance().getReference("profile_images")
                .child(
                    auth.currentUser?.uid.toString()
                            + "-" + System.currentTimeMillis()
                            + "." + getFileExtension(imageUri!!)
                )

            fileReference.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        updateProfileImage(uri)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengunggah gambar.", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = contentResolver
        val mime = android.webkit.MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri)) ?: "jpg"
    }

    private fun updateProfileImage(uri: Uri) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(this).clear(binding.profileImage)

                    Glide.with(this)
                        .load(uri)
                        .into(binding.profileImage)
                    sessionManager.saveImageUrl(uri.toString())
                    saveProfileImageUriToDatabase(uri.toString())
                }
            }
    }

    private fun saveProfileImageUriToDatabase(imageUrl: String?) {
        val userUid = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().getReference("Users").child(userUid)

        val updates = hashMapOf<String, Any>("imageUrl" to (imageUrl ?: ""))

        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profil gambar diperbarui.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
