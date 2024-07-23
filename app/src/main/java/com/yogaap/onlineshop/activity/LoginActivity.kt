package com.yogaap.onlineshop.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.Model.UsersModel
import com.yogaap.onlineshop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef = firebaseDatabase.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.loginRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.loginEmail.text.toString().trim()
        val password = binding.loginPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.loginEmail.error = "Email is required"
            binding.loginEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.loginPassword.error = "Password is required"
            binding.loginPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        usersRef.child(it.uid).get().addOnCompleteListener { userTask ->
                            if (userTask.isSuccessful) {
                                val userSnapshot = userTask.result
                                val user = userSnapshot.getValue(UsersModel::class.java)
                                if (user != null) {
                                    sessionManager.saveUserSession(user.name, user.email)
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    binding.loginEmail.error = "User not found"
                                    binding.loginEmail.requestFocus()
                                }
                            } else {
                                binding.loginEmail.error = "Failed to retrieve user"
                                binding.loginEmail.requestFocus()
                            }
                        }
                    }
                } else {
                    binding.loginEmail.error = "Email or Password is wrong"
                    binding.loginEmail.requestFocus()
                }
            }
    }
}