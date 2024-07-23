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
    private val scrollDelay = 350L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scrollView = binding.listScrollView
        val bottomNavigationView = binding.bottomNavigationView

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY != oldScrollY) {
                bottomNavigationView.bottomNavigation.visibility = View.GONE
                isScrolling = true
                scrollRunnable?.let { handler.removeCallbacks(it) }
                scrollRunnable = Runnable {
                    if (isScrolling) {
                        bottomNavigationView.bottomNavigation.visibility = View.VISIBLE
                        isScrolling = false
                    }
                }
                handler.postDelayed(scrollRunnable!!, scrollDelay)
            }
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            if (scrollY == scrollView.scrollY && isScrolling) {
                handler.removeCallbacks(scrollRunnable!!)
                scrollRunnable = Runnable {
                    bottomNavigationView.bottomNavigation.visibility = View.VISIBLE
                    isScrolling = false
                }
                handler.postDelayed(scrollRunnable!!, scrollDelay)
            }
        }

        val type = intent.getStringExtra("TYPE")

        when (type) {
            "BRAND" -> initBrand()
            "RECOMMEND" -> initRecommend()
        }

        binding.listBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun initBrand() {
        binding.progressBarList.visibility = View.VISIBLE
        viewModel.brands.observe(this, { brands ->
            val layoutManager = if (brands.size < 10) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2)
            }
            binding.viewList.layoutManager = layoutManager
            binding.viewList.adapter = BrandAdapter(brands, binding.viewList, true)
            binding.progressBarList.visibility = View.GONE
        })
        viewModel.loadBrand()
    }

    private fun initRecommend() {
        binding.progressBarList.visibility = View.VISIBLE
        viewModel.recommends.observe(this, { recommends ->
            val layoutManager = if (recommends.size < 10) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2)
            }
            binding.viewList.layoutManager = layoutManager
            binding.viewList.adapter = RecommendAdapter(recommends, true)
            binding.progressBarList.visibility = View.GONE
        })
        viewModel.loadRecommendation()
    }
}