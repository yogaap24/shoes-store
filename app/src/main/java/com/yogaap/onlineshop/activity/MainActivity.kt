package com.yogaap.onlineshop.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.yogaap.onlineshop.Adapter.BrandAdapter
import com.yogaap.onlineshop.Adapter.RecommendAdapter
import com.yogaap.onlineshop.Adapter.SliderAdapter
import com.yogaap.onlineshop.Model.SliderModel
import com.yogaap.onlineshop.ViewModel.MainViewModel
import com.yogaap.onlineshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel = MainViewModel()
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isScrolling = false
    private var scrollRunnable: Runnable? = null
    private val scrollDelay = 350L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scrollView = binding.mainScrollView
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

        initBanner()
        initBrand()
        seeAllBrand()
        initRecommend()
        seeAllRecommend()
    }

    private fun initBanner() {
        binding.dashProgressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this, { items ->
            banners(items)
            binding.dashProgressBarBanner.visibility = View.GONE
        })
        viewModel.loadBanner()
    }

    private fun banners(images: List<SliderModel>) {
        binding.dashViewPagerSlider.adapter = SliderAdapter(images, binding.dashViewPagerSlider)
        binding.dashViewPagerSlider.clipToPadding = false
        binding.dashViewPagerSlider.clipChildren = false
        binding.dashViewPagerSlider.offscreenPageLimit = 3
        binding.dashViewPagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }
        binding.dashViewPagerSlider.setPageTransformer(compositePageTransformer)
        if (images.size > 1) {
            binding.dashDotsIndicator.visibility = View.VISIBLE
            binding.dashDotsIndicator.attachTo(binding.dashViewPagerSlider)
        }
    }

    private fun seeAllBrand() {
        binding.dashSeeAllBrand.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("TYPE", "BRAND")
            startActivity(intent)
        }
    }

    private fun initBrand() {
        binding.dashProgressBarBrand.visibility = View.VISIBLE
        viewModel.brands.observe(this, {
            binding.dashViewBrand.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.dashViewBrand.adapter = BrandAdapter(it, binding.dashViewBrand, false)
            binding.dashProgressBarBrand.visibility = View.GONE
        })
        viewModel.loadBrand()
    }

    private fun initRecommend() {
        binding.dashProgressBarRecommend.visibility = View.VISIBLE
        viewModel.recommends.observe(this, {
            binding.dashViewRecommend.layoutManager =
                GridLayoutManager(this, 2)
            binding.dashViewRecommend.adapter = RecommendAdapter(it, false)
            binding.dashProgressBarRecommend.visibility = View.GONE
        })
        viewModel.loadRecommendation()
    }

    private fun seeAllRecommend() {
        binding.dashSeeAllRecommend.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("TYPE", "RECOMMEND")
            startActivity(intent)
        }
    }
}