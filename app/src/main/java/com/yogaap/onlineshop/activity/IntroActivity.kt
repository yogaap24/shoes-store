package com.yogaap.onlineshop.activity

import android.content.Intent
import android.os.Bundle
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.introSignInText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        sessionManager = SessionManager(this)
        if (sessionManager.getUserSession() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}