package com.yogaap.onlineshop.Model

data class ItemModel(
    val Id: Int = 0,
    val title: String = "",
    val description: String = "",
    val picUrl: ArrayList<String> = ArrayList(),
    val size : ArrayList<String> = ArrayList(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val numberIncart: Int = 0,
)
