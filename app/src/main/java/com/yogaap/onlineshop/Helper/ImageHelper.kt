package com.yogaap.onlineshop.Helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageHelper {

    const val PICK_IMAGE_REQUEST = 1

    fun openImageChooser(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun compressImage(context: Context, uri: Uri): Uri {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file)
    }

    private fun getFileExtension(uri: Uri): String? {
        return uri.path?.substring(uri.path!!.lastIndexOf(".") + 1)
    }

    fun uploadImage(uri: Uri, callback: (String?, String?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val fileReference = storageReference.child(
            "images_profile/${System.currentTimeMillis()}" +
                    "-${UUID.randomUUID()}" +
                    ".${getFileExtension(uri)}"
        )

        fileReference.putFile(uri)
            .addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    callback(downloadUri.toString(), null)
                }.addOnFailureListener {
                    callback(null, it.message)
                }
            }
            .addOnFailureListener {
                callback(null, it.message)
            }
    }
}