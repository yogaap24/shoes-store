package com.yogaap.onlineshop.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.databinding.ActivityViewImageBinding

class ViewImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.fullImageView)
        } else {
            binding.fullImageView.setImageResource(R.drawable.img_profile)
        }
    }
}