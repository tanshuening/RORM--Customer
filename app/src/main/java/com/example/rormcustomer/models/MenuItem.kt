package com.example.rormcustomer.models

data class MenuItem(
    var foodId: String = "",
    val foodName: String = "",
    val foodPrice: String = "",
    val foodDescription: String = "",
    val foodIngredients: String = "",
    val foodImage: String = "",
    var available: Boolean = true,
    val restaurantId: String = ""
)
