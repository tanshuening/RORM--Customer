package com.example.rormcustomer.models

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val orderItems: List<OrderItem> = listOf(),
    val totalAmount: Double = 0.0,
    val orderStatus: String = "Pending",
    val orderDate: String = "",
    val specialRequests: String? = null,
    val paymentMethod: String = "Card",
    val deliveryAddress: String? = null
)
