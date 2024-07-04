package com.example.rormcustomer.models

data class User(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profileImageUrl: String? = null,
    val loyaltyPoints: Int = 0
)
