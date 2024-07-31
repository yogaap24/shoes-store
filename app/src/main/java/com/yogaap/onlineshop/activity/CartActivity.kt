package com.yogaap.onlineshop.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagementCart
import com.yogaap.onlineshop.Adapter.CartAdapter
import com.yogaap.onlineshop.databinding.ActivityCartBinding

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var managementCart: ManagementCart

    private var tax : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        managementCart = ManagementCart(this)

        binding.cartBackBtn.setOnClickListener {
            finish()
        }
        initCartList()
        calculateCart()
    }

    private fun initCartList() {
        binding.cartViewList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.cartViewList.adapter = CartAdapter(
            managementCart.getListCart(),
            this,
            object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()
                }
            }
        )

        with(binding) {
            cartEmptyTxtVw.visibility =
                if (managementCart.getListCart().isEmpty()) View.VISIBLE else View.GONE

            cartScrollView.visibility =
                if (managementCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun calculateCart() {
        val percentTax = 2.5
        val deliveryCost = 5000

        tax = managementCart.getTotalFee() * percentTax / 100 // 2.5% tax

        val totalPrice = managementCart.getTotalFee() + tax + deliveryCost
        val totalItem = managementCart.getTotalFee()

        with(binding) {
            cartSubTtlAmount.text   = "Rp. $totalItem"
            cartTaxAmount.text      = "Rp. $tax"
            cartDeliveryAmount.text = "Rp. $deliveryCost"
            cartTotalAmount.text    = "Rp. $totalPrice"
        }

    }
}