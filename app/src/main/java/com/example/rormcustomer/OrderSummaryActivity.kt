package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.OrderSummaryMenuItemAdapter
import com.example.rormcustomer.databinding.ActivityOrderSummaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var orderQuery: Query
    private lateinit var restaurantRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: OrderSummaryMenuItemAdapter
    private val items = mutableListOf<String>()
    private val prices = mutableListOf<Double>()
    private val quantities = mutableListOf<Int>()
    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid ?: return
        restaurantId = intent.getStringExtra("restaurantId")
        Log.d("OrderSummaryActivity", "Received Restaurant ID: $restaurantId")
        if (restaurantId == null) {
            Log.e("OrderSummaryActivity", "Restaurant ID is null")
            finish()
            return
        }
        orderQuery = database.getReference("orders").orderByChild("userId").equalTo(userId)
        restaurantRef = database.getReference("restaurants").child(restaurantId!!)

        adapter = OrderSummaryMenuItemAdapter(items, prices, quantities, restaurantId!!)

        binding.orderSummaryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.orderSummaryRecyclerView.adapter = adapter

        loadOrderData()
        loadRestaurantName()

        binding.orderSummaryAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        val paymentClickListener = View.OnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            startActivityForResult(intent, PAYMENT_METHOD_REQUEST_CODE)
        }

        binding.paymentMethod.setOnClickListener(paymentClickListener)
        binding.paymentMethodText.setOnClickListener(paymentClickListener)
        binding.paymentMethodImage.setOnClickListener(paymentClickListener)

        val reservationClickListener = View.OnClickListener {
            val intent = Intent(this, UpcomingReservationActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            startActivity(intent)
        }

        binding.reservation.setOnClickListener(reservationClickListener)
        binding.reservationText.setOnClickListener(reservationClickListener)
        binding.reservationImage.setOnClickListener(reservationClickListener)

    }

    private fun loadOrderData() {
        orderQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                prices.clear()
                quantities.clear()

                for (orderSnapshot in snapshot.children) {
                    val restaurantIdFromDb = orderSnapshot.child("restaurantId").getValue(String::class.java)
                    if (restaurantIdFromDb == restaurantId) {
                        val itemsSnapshot = orderSnapshot.child("items")
                        for (itemSnapshot in itemsSnapshot.children) {
                            val item = itemSnapshot.child("foodName").getValue(String::class.java)
                            val price = itemSnapshot.child("foodPrice").getValue(String::class.java)?.toDouble()
                            val quantity = itemSnapshot.child("quantity").getValue(Int::class.java)

                            item?.let { items.add(it) }
                            price?.let { prices.add(it) }
                            quantity?.let { quantities.add(it) }
                        }
                    }
                }

                adapter.notifyDataSetChanged()
                calculateTotals()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderSummaryActivity", "Error loading order data: ${error.message}")
            }
        })
    }

    private fun calculateTotals() {
        var subtotal = 0.0
        for (i in prices.indices) {
            subtotal += prices[i] * quantities[i]
        }
        binding.subtotalAmount.text = String.format("%.2f", subtotal)
        binding.totalAmount.text = String.format("%.2f", subtotal)
    }

    private fun loadRestaurantName() {
        restaurantRef.child("name").get().addOnSuccessListener { snapshot ->
            val restaurantName = snapshot.getValue(String::class.java)
            binding.restaurantName.text = restaurantName
        }.addOnFailureListener {
            Log.e("OrderSummaryActivity", "Failed to load restaurant name", it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_METHOD_REQUEST_CODE && resultCode == RESULT_OK) {
            val paymentMethod = data?.getStringExtra("paymentMethod")
            binding.paymentMethodText.text = paymentMethod
        }
    }

    companion object {
        private const val PAYMENT_METHOD_REQUEST_CODE = 1
    }
}