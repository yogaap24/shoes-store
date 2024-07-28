package com.yogaap.onlineshop.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yogaap.onlineshop.Adapter.BrandAdapter
import com.yogaap.onlineshop.Adapter.RecommendAdapter
import com.yogaap.onlineshop.Adapter.SliderAdapter
import com.yogaap.onlineshop.Helper.SessionManager
import com.yogaap.onlineshop.Helper.setupNavigation
import com.yogaap.onlineshop.Model.SliderModel
import com.yogaap.onlineshop.R
import com.yogaap.onlineshop.ViewModel.MainViewModel
import com.yogaap.onlineshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel = MainViewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var isScrolling = false
    private var scrollRunnable: Runnable? = null
    private val scrollDelay = 350L
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scrollView = binding.mainScrollView
        val dashNavigation = binding.dashNavigation.bottomNavigationLayout
        bottomNavigationView = dashNavigation.findViewById(R.id.bottomNavigation)

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY != oldScrollY) {
                dashNavigation.visibility = View.GONE
                isScrolling = true
                scrollRunnable?.let { handler.removeCallbacks(it) }
                scrollRunnable = Runnable {
                    if (isScrolling) {
                        dashNavigation.visibility = View.VISIBLE
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
                    dashNavigation.visibility = View.VISIBLE
                    isScrolling = false
                }
                handler.postDelayed(scrollRunnable!!, scrollDelay)
            }
        }

        sessionManager = SessionManager(this)

        isUserLoggedIn()
        initBanner()
        initBrand()
        seeAllBrand()
        initRecommend()
        seeAllRecommend()

        checkAndRequestPermissions()

        bottomNavigationView.setupNavigation(this)
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

    private fun isUserLoggedIn() {
        sessionManager.getUserSession()?.let {
            binding.dashUserName.text = it.name
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        } else {
            val permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val permissionsToRequest = permissions.filter {
                checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED
            }

            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all {
                    it == PackageManager.PERMISSION_GRANTED
                }) {
                Toast.makeText(this, "Semua izin diberikan.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Izin Diperlukan")
        builder.setMessage(
            "Izin penyimpanan diperlukan untuk menggunakan aplikasi ini. " +
                    "Harap izinkan melalui Pengaturan."
        )
        builder.setPositiveButton("Pengaturan") { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Batal") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}