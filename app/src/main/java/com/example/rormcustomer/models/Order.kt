package com.example.rormcustomer.models

data class Order(
    val orderId: String = "",
    val orderItems: List<OrderItem> = listOf(),
    val totalAmount: Double = 0.0,
    val subtotalAmount: Double = 0.0,
    val orderStatus: String = "Pending",
    val orderDate: String = "",
    val specialRequests: String? = null,
    val paymentMethod: String = "",
    val promotionId: String = "",
    val reservationId: String = "",
)