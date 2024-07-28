package com.yogaap.onlineshop.Helper

import android.content.Context
import android.content.SharedPreferences
import com.yogaap.onlineshop.Model.UsersModel

class SessionManager(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_session"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IMAGE_URL  = "image_url"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSession(userName: String, userEmail: String) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.apply()
    }

    fun getUserSession(): UsersModel? {
        val name = prefs.getString(KEY_USER_NAME, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)
        return if (name != null && email != null) {
            UsersModel(name, email)
        } else {
            null
        }
    }

    fun clearUserSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun saveImageUrl(imageUrl: String) {
        prefs.edit().putString(KEY_IMAGE_URL, imageUrl).apply()
    }

    fun getImageUrl(): String? {
        return prefs.getString(KEY_IMAGE_URL, null)
    }
}