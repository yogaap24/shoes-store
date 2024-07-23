package com.yogaap.onlineshop.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogaap.onlineshop.Adapter.BrandAdapter
import com.yogaap.onlineshop.Adapter.RecommendAdapter
import com.yogaap.onlineshop.ViewModel.MainViewModel
import com.yogaap.onlineshop.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private val viewModel = MainViewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var isScrolling = false
    private var scrollRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scrollView = binding.listScrollView
        val bottomNavigationView = binding.bottomNavigationView

        scrollView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY || scrollY < oldScrollY) {
                bottomNavigationView.bottomNavigation.visibility = View.GONE
                isScrolling = true
                scrollRunnable?.let { handler.removeCallbacks(it) }
                scrollRunnable = Runnable {
                    if (isScrolling) {
                        bottomNavigationView.bottomNavigation.visibility = View.VISIBLE
                        isScrolling = false
                    }
                }
                handler.postDelayed(scrollRunnable!!, 300)
            }
        }

        val type = intent.getStringExtra("TYPE")

        when (type) {
            "BRAND" -> initBrand()
            "RECOMMEND" -> initRecommend()
        }
    }

    private fun initBrand() {
        binding.progressBarBrand.visibility = View.VISIBLE
        viewModel.brands.observe(this, { brands ->
            val layoutManager = if (brands.size < 10) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2)
            }
            binding.viewBrand.layoutManager = layoutManager
            binding.viewBrand.adapter = BrandAdapter(brands, binding.viewBrand, true)
            binding.progressBarBrand.visibility = View.GONE
        })
        viewModel.loadBrand()
    }

    private fun initRecommend() {
        binding.progressBarBrand.visibility = View.VISIBLE
        viewModel.recommends.observe(this, { recommends ->
            val layoutManager = if (recommends.size < 10) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2)
            }
            binding.viewBrand.layoutManager = layoutManager
            binding.viewBrand.adapter = RecommendAdapter(recommends, true)
            binding.progressBarBrand.visibility = View.GONE
        })
        viewModel.loadRecommendation()
    }
}