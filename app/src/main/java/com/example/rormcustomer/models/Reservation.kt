package com.example.rormcustomer.models

data class Reservation(
    val reservationId: String = "",
    val restaurantId: String = "",
    val userId: String = "",
    val date: Long = 0L,
    val timeSlot: String = "",
    val numOfPax: Int = 1,
    val specialRequest: String? = null,
    val bookingOccasion: String? = null,
    val bookingPhone: String? = null,
    val order: Order? = null,
    var restaurant: Restaurant? = null
)
