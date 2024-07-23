package com.yogaap.onlineshop.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.project1762.Helper.ManagementCart
import com.yogaap.onlineshop.Adapter.ColorAdapter
import com.yogaap.onlineshop.Adapter.SizeAdapter
import com.yogaap.onlineshop.Adapter.SliderAdapter
import com.yogaap.onlineshop.Model.ItemsModel
import com.yogaap.onlineshop.Model.SliderModel
import com.yogaap.onlineshop.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity(), ColorAdapter.OnColorSelectedListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var productSlider: ArrayList<SliderModel>

    private var orderNumber = 1
    private lateinit var managementCart: ManagementCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managementCart = ManagementCart(this)

        getBundle()
        banners()
        initLists()
    }

    private fun banners() {
        productSlider = ArrayList()
        for (imageUrl in item.picUrl) {
            productSlider.add(SliderModel(imageUrl))
        }

        binding.productBanner.adapter = SliderAdapter(productSlider, binding.productBanner)
        binding.productBanner.clipToPadding = true
        binding.productBanner.clipChildren = true
        binding.productBanner.offscreenPageLimit = productSlider.size

        if (productSlider.size > 1) {
            binding.productDotsIndicator.visibility = android.view.View.VISIBLE
            binding.productDotsIndicator.attachTo(binding.productBanner)
        }

        binding.productBanner.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Sync the selected color with the ViewPager position
                val selectedColor = productSlider[position].url
                (binding.productColorList.adapter as? ColorAdapter)?.setSelectedColor(selectedColor)
            }
        })
    }

    private fun getBundle() {
        item = intent.getParcelableExtra("item_object")!!

        binding.productTitle.text = item.title
        binding.productDesc.text = item.description
        binding.productPrice.text = "Rp" + item.price
        binding.productRating.text = "${item.rating} Rating"
        binding.productAddCart.setOnClickListener {
            item.numberInCart = orderNumber
            managementCart.insertItem(item)
        }
        binding.productBack.setOnClickListener {
            finish()
        }
        binding.productAddCart.setOnClickListener {

        }
    }

    private fun initLists() {
        val productSizeList = ArrayList<String>()
        for (size in item.size) {
            productSizeList.add(size.toString())
        }

        binding.productSizeList.adapter = SizeAdapter(productSizeList, binding.productSizeList)
        binding.productSizeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val productColorList = ArrayList<String>()
        for (color in item.picUrl) {
            productColorList.add(color)
        }

        binding.productColorList.adapter =
            ColorAdapter(productColorList, binding.productColorList, this)
        binding.productColorList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onColorSelected(picUrl: String) {
        val position = productSlider.indexOfFirst { it.url == picUrl }
        if (position != -1) {
            binding.productBanner.setCurrentItem(position, true)
        }
    }
}

