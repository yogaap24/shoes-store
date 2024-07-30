package com.yogaap.onlineshop.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.Model.UsersModel
import com.yogaap.onlineshop.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef = firebaseDatabase.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        binding.registerButton.setOnClickListener {
            register()
        }

        binding.registerLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun register() {
        val name = binding.registerName.text.toString().trim()
        val email = binding.registerEmail.text.toString().trim()
        val password = binding.registerPassword.text.toString().trim()
        val confirmPassword = binding.registerRePassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.registerName.error = "Name is required"
            binding.registerName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.registerEmail.error = "Email is required"
            binding.registerEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.registerPassword.error = "Password is required"
            binding.registerPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.registerRePassword.error = "Confirm Password is required"
            binding.registerRePassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.registerRePassword.error = "Password not match"
            binding.registerRePassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUser(name, email)
                } else {
                    binding.registerEmail.error = "Email already registered"
                    binding.registerEmail.requestFocus()
                }
            }
    }

    private fun saveUser(name: String, email: String) {
        val firebaseUser = auth.currentUser
        firebaseUser?.let {
            val user = UsersModel(name, email)
            usersRef.child(it.uid).setValue(user).addOnCompleteListener{
                if (it.isSuccessful) {
                    sessionManager.saveUserSession(name, email)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}