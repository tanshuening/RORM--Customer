package com.example.rormcustomer.models

data class Restaurant(
    val restaurantId: String? = null,
    val name: String? = null,
    val location: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val price: String? = null,
    val cuisine: String? = null,
    val payment: String? = null,
    val parking: String? = null,
    val dressCode: String? = null,
    val description: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val userId: String? = null,
    val images: List<String>? = null,
    val feedback: List<Feedback>? = null
)
