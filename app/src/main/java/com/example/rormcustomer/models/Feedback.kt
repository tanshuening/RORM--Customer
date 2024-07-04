package com.example.rormcustomer.models

data class Feedback(
    val feedbackId: String?,
    val userId: String,
    val restaurantId: String,
    val reservationId: String,
    val rating: Int,
    val review: String,
    val timestamp: Long
)
