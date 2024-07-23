package com.yogaap.onlineshop.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogaap.onlineshop.Model.BrandsModel
import com.yogaap.onlineshop.Model.ItemsModel
import com.yogaap.onlineshop.Model.SliderModel

class MainViewModel() : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _brand = MutableLiveData<MutableList<BrandsModel>>()
    private val _recommend = MutableLiveData<MutableList<ItemsModel>>()

    val banners: LiveData<List<SliderModel>> = _banner
    val brands: LiveData<MutableList<BrandsModel>> = _brand
    val recommends: LiveData<MutableList<ItemsModel>> = _recommend

    fun loadBanner() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (data in snapshot.children) {
                    val item = data.getValue(SliderModel::class.java)
                    item?.let { lists.add(item) }
                }
                _banner.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error: ${error.message}")
            }
        })
    }

    fun loadBrand() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandsModel>()
                for (data in snapshot.children) {
                    val item = data.getValue(BrandsModel::class.java)
                    item?.let { lists.add(item) }
                }
                _brand.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error: ${error.message}")
            }
        })
    }

    fun loadRecommendation() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (data in snapshot.children) {
                    val item = data.getValue(ItemsModel::class.java)
                    item?.let { lists.add(item) }
                }
                _recommend.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel", "Error: ${error.message}")
            }
        })
    }
}