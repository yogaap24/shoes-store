package com.yogaap.onlineshop.ViewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogaap.onlineshop.Helper.ImageHelper
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.Model.UsersModel
import com.yogaap.onlineshop.activity.ViewImageActivity

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val sessionManager = SessionManager(getApplication())

    private val _userProfile = MutableLiveData<UsersModel?>()
    val userProfile: LiveData<UsersModel?> = _userProfile

    private val _imageUploadResult = MutableLiveData<Pair<String?, String?>>()
    val imageUploadResult: LiveData<Pair<String?, String?>> = _imageUploadResult

    private val _imageDeleteResult = MutableLiveData<Boolean>()
    val imageDeleteResult: LiveData<Boolean> = _imageDeleteResult

    private var userDatabaseReference: DatabaseReference? = null
    private var userListener: ValueEventListener? = null

    fun loadUserProfile() {
        val user = firebaseAuth.currentUser ?: return
        val userUid = user.uid

        // Set up new listener
        userDatabaseReference = firebaseDatabase.getReference("Users").child(userUid)
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(UsersModel::class.java)
                if (userProfile != null) {
                    _userProfile.value = userProfile
                    sessionManager.saveUserSession(userProfile.name, userProfile.email)

                    val imageUrl = userProfile.imageUrl
                    if (imageUrl != null) {
                        sessionManager.saveImageUrl(imageUrl)
                        _imageUploadResult.value = Pair(imageUrl, null)
                    } else {
                        sessionManager.clearImageUrl() // Pastikan cache dihapus saat imageUrl null
                        _imageUploadResult.value = Pair(null, null)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    "ProfileViewModel",
                    "Gagal memuat data pengguna dari database",
                    error.toException()
                )
            }
        }
        userDatabaseReference!!.addValueEventListener(userListener!!)

        val cachedUser = sessionManager.getUserSession()
        val cachedImageUrl = sessionManager.getImageUrl()

        if (cachedUser != null) {
            _userProfile.value = cachedUser
            _imageUploadResult.value = Pair(cachedImageUrl, null)
        }
    }

    fun uploadProfileImage(uri: Uri) {
        ImageHelper.uploadImage(uri) { imageUrl, error ->
            if (error != null) {
                _imageUploadResult.value = Pair(null, error)
                Log.e("ProfileViewModel", "Gagal mengunggah gambar profil: $error")
            } else {
                if (imageUrl != null) {
                    sessionManager.saveImageUrl(imageUrl)
                    updateProfileImage(imageUrl)
                }
            }
        }
    }

    private fun updateProfileImage(imageUrl: String) {
        val user = firebaseAuth.currentUser ?: return
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(imageUrl))
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userUid = user.uid
                val databaseReference = firebaseDatabase.getReference("Users").child(userUid)

                databaseReference.child("imageUrl").setValue(imageUrl)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            _imageUploadResult.value = Pair(imageUrl, null)
                            sessionManager.saveImageUrl(imageUrl)
                            Toast.makeText(
                                getApplication(),
                                "Gambar profil berhasil diperbarui",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e(
                                "ProfileViewModel",
                                "Gagal memperbarui URL gambar di database"
                            )
                        }
                    }
            } else {
                Toast.makeText(
                    getApplication(),
                    "Gagal memperbarui gambar profil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun deleteProfileImage() {
        val user = firebaseAuth.currentUser ?: return
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(null)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userUid = user.uid
                val databaseReference = firebaseDatabase.getReference("Users").child(userUid)

                databaseReference.child("imageUrl").removeValue()
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            sessionManager.clearImageUrl()
                            Toast.makeText(
                                getApplication(),
                                "Gambar profil berhasil dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            _imageDeleteResult.value = true

                            databaseReference.addValueEventListener(userListener!!)
                        } else {
                            Log.e(
                                "ProfileViewModel", "Gagal menghapus URL gambar di database"
                            )
                            _imageDeleteResult.value = false
                        }
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(
                getApplication(),
                "Gagal menghapus gambar profil",
                Toast.LENGTH_SHORT
            ).show()
            _imageDeleteResult.value = false
        }
    }

    fun viewProfileImage(context: Context) {
        val imageUrl = sessionManager.getImageUrl() ?: return
        val intent = Intent(context, ViewImageActivity::class.java).apply {
            putExtra("IMAGE_URL", imageUrl)
        }
        context.startActivity(intent)
    }

    override fun onCleared() {
        super.onCleared()
        userDatabaseReference?.removeEventListener(userListener!!)
    }
}