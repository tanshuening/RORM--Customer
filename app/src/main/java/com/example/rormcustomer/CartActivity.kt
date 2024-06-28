package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.CartAdapter
import com.example.rormcustomer.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantName = listOf("iVegan", "Sando")
        val quantity = listOf("1 item", "2 items")
        val restaurantImage = listOf(
            R.drawable.restaurant,
            R.drawable.restaurant2
        )

        val adapter = CartAdapter(ArrayList(restaurantName), ArrayList(quantity), ArrayList(restaurantImage))
        binding.cartRestaurantRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRestaurantRecyclerView.adapter = adapter

        binding.cartAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
