package com.example.rormcustomer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.ActivityMenuItemInfoBinding
import com.example.rormcustomer.models.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MenuItemInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuItemInfoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItemRef: DatabaseReference
    private lateinit var orderRef: DatabaseReference
    private var menuItemId: String? = null
    private lateinit var auth: FirebaseAuth
    private var number = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuItemInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        menuItemId = intent.getStringExtra("menuItemId")
        if (menuItemId != null) {
            val restaurantId = intent.getStringExtra("restaurantId")
            if (restaurantId != null) {
                menuItemRef = database.getReference("restaurants").child(restaurantId).child("menu").child(menuItemId!!)
                retrieveAndDisplayMenuItem()
            } else {
                Log.e("MenuItemInfoActivity", "Restaurant ID is null")
                finish()
            }
        } else {
            Log.e("MenuItemInfoActivity", "MenuItemId is null")
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        with(binding) {
            plusButton.setOnClickListener {
                number++
                numberTextView.text = number.toString()
            }

            minusButton.setOnClickListener {
                if (number > 0) {
                    number--
                    numberTextView.text = number.toString()
                }
            }

            addToBasketButton.setOnClickListener {
                addToBasket()
            }
        }
    }

    private fun retrieveAndDisplayMenuItem() {
        menuItemRef.get().addOnSuccessListener { snapshot ->
            val menuItem = snapshot.getValue(MenuItem::class.java)
            Log.d("MenuItemInfoActivity", "Retrieved menu item: $menuItem")
            if (menuItem != null) {
                with(binding) {
                    menuItemNameTextView.text = menuItem.foodName
                    menuItemPriceTextView.text = menuItem.foodPrice
                    menuItemDescriptionTextView.text = menuItem.foodDescription
                    menuItemIngredientsTextView.text = menuItem.foodIngredients
                    Glide.with(this@MenuItemInfoActivity).load(Uri.parse(menuItem.foodImage))
                        .into(menuItemImageView)
                }
                Log.d("MenuItemInfoActivity", "Menu item retrieved successfully: $menuItem")
            } else {
                Log.e("MenuItemInfoActivity", "Menu item is null")
            }
        }.addOnFailureListener {
            Log.e("MenuItemInfoActivity", "Failed to retrieve menu item", it)
        }
    }

    private fun addToBasket() {
        val userId = auth.currentUser?.uid ?: return
        val restaurantId = intent.getStringExtra("restaurantId") ?: return

        orderRef = database.getReference("orders")
        val query = orderRef.orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var itemFound = false
                for (orderSnapshot in snapshot.children) {
                    val orderRestaurantId = orderSnapshot.child("restaurantId").getValue(String::class.java)
                    if (orderRestaurantId == restaurantId) {
                        val itemsSnapshot = orderSnapshot.child("items")
                        for (itemSnapshot in itemsSnapshot.children) {
                            val existingItemName = itemSnapshot.child("foodName").getValue(String::class.java)
                            if (existingItemName == binding.menuItemNameTextView.text.toString()) {
                                val existingQuantity = itemSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                                val newQuantity = existingQuantity + number
                                itemSnapshot.ref.child("quantity").setValue(newQuantity)
                                itemFound = true
                                break
                            }
                        }
                        if (!itemFound) {
                            val newItemRef = itemsSnapshot.ref.push()
                            newItemRef.setValue(
                                mapOf(
                                    "foodName" to binding.menuItemNameTextView.text.toString(),
                                    "foodPrice" to binding.menuItemPriceTextView.text.toString(),
                                    "quantity" to number
                                )
                            )
                        }
                        break
                    }
                }
                if (!itemFound) {
                    val newOrderRef = orderRef.push()
                    val orderData = mapOf(
                        "userId" to userId,
                        "restaurantId" to restaurantId,
                        "items" to listOf(
                            mapOf(
                                "foodName" to binding.menuItemNameTextView.text.toString(),
                                "foodPrice" to binding.menuItemPriceTextView.text.toString(),
                                "quantity" to number
                            )
                        )
                    )
                    newOrderRef.setValue(orderData)
                }
                Log.d("MenuItemInfoActivity", "Added to basket: ${binding.menuItemNameTextView.text} with quantity $number")
                // Navigate back to the previous screen
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuItemInfoActivity", "Failed to add to basket", error.toException())
            }
        })
    }
}