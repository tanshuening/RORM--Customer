package com.example.rormcustomer.models

data class OrderItem(
    val itemId: String = "",
    val itemName: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
)