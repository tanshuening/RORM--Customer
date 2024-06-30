package com.example.rormcustomer

import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.*

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
    private var orderId: String? = null
    private var paymentMethod: String? = null
    private var promotionDiscount: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        promotionDiscount = intent.getStringExtra("promotionDiscount")?.toDoubleOrNull()
        restaurantId = intent.getStringExtra("restaurantId")

        Log.d("OrderSummaryActivity", "Received Restaurant ID: $restaurantId")
        if (restaurantId == null) {
            Log.e("OrderSummaryActivity", "Restaurant ID is null")
            finish()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        setupReservationDetails()
        setupRecyclerView()
        setupClickListeners()
        loadSavedPromotionName()
        restorePaymentMethod(savedInstanceState)

        orderQuery = database.getReference("orders").orderByChild("userId").equalTo(userId)
        restaurantRef = database.getReference("restaurants").child(restaurantId!!)

        loadOrderData()
        loadRestaurantName()
    }

    private fun setupReservationDetails() {
        val numOfPax = intent.getIntExtra("numOfPax", 0)
        val bookingDate = intent.getLongExtra("bookingDate", 0L)
        val bookingTime = intent.getStringExtra("bookingTime")

        if (numOfPax != 0 && bookingDate != 0L && bookingTime != null) {
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(bookingDate))
            binding.reservationText.text = "$numOfPax pax, $dateFormatted, $bookingTime"
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderSummaryMenuItemAdapter(items, prices, quantities, restaurantId!!)
        binding.orderSummaryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.orderSummaryRecyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        val backClickListener = View.OnClickListener { onBackPressed() }
        binding.orderSummaryAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener(backClickListener)

        val paymentClickListener = View.OnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("currentPaymentMethod", paymentMethod)
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

        val promotionClickListener = View.OnClickListener {
            val intent = Intent(this, PromotionActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            startActivity(intent)
        }

        binding.promotions.setOnClickListener(promotionClickListener)
        binding.promotionsText.setOnClickListener(promotionClickListener)
        binding.promotionsImage.setOnClickListener(promotionClickListener)
    }

    private fun loadSavedPromotionName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val savedPromotionName = sharedPref.getString("promotionName", null)
        savedPromotionName?.let {
            binding.promotionsText.text = it
        }
    }

    private fun restorePaymentMethod(savedInstanceState: Bundle?) {
        paymentMethod = savedInstanceState?.getString("paymentMethod")
            ?: loadPaymentMethodFromDatabase()
        binding.paymentMethodText.text = paymentMethod ?: "Payment Method"
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
                        orderId = orderSnapshot.key
                        val itemsSnapshot = orderSnapshot.child("items")
                        for (itemSnapshot in itemsSnapshot.children) {
                            itemSnapshot.child("foodName").getValue(String::class.java)?.let { items.add(it) }
                            itemSnapshot.child("foodPrice").getValue(String::class.java)?.toDoubleOrNull()?.let { prices.add(it) }
                            itemSnapshot.child("quantity").getValue(Int::class.java)?.let { quantities.add(it) }
                        }

                        paymentMethod = orderSnapshot.child("paymentMethod").getValue(String::class.java)
                        binding.paymentMethodText.text = paymentMethod ?: "Select Payment Method"
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
        var subtotal = prices.zip(quantities).sumOf { it.first * it.second }

        binding.subtotalAmount.text = String.format("%.2f", subtotal)

        val total = promotionDiscount?.let { discount ->
            subtotal - (subtotal * discount / 100)
        } ?: subtotal

        binding.totalAmount.text = String.format("%.2f", total)
        //binding.discountAmount.text = String.format("%.2f", promotionDiscount ?: 0.0)
    }

    private fun loadRestaurantName() {
        restaurantRef.child("name").get().addOnSuccessListener { snapshot ->
            snapshot.getValue(String::class.java)?.let {
                binding.restaurantName.text = it
            }
        }.addOnFailureListener {
            Log.e("OrderSummaryActivity", "Failed to load restaurant name", it)
        }
    }

    private fun loadPaymentMethodFromDatabase(): String? {
        var paymentMethodFromDb: String? = null
        orderId?.let { id ->
            val orderRef = database.getReference("orders").child(id)
            orderRef.child("paymentMethod").get().addOnSuccessListener { snapshot ->
                paymentMethodFromDb = snapshot.getValue(String::class.java)
                binding.paymentMethodText.text = paymentMethodFromDb ?: "Payment Method"
            }.addOnFailureListener {
                Log.e("OrderSummaryActivity", "Failed to load payment method", it)
            }
        }
        return paymentMethodFromDb
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("paymentMethod", paymentMethod)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_METHOD_REQUEST_CODE && resultCode == RESULT_OK) {
            paymentMethod = data?.getStringExtra("paymentMethod")
            binding.paymentMethodText.text = paymentMethod
            savePaymentMethodToDatabase(paymentMethod)
        }
    }

    private fun savePaymentMethodToDatabase(paymentMethod: String?) {
        if (orderId != null && paymentMethod != null) {
            val orderRef = database.getReference("orders").child(orderId!!)
            orderRef.child("paymentMethod").setValue(paymentMethod)
                .addOnSuccessListener {
                    Log.d("OrderSummaryActivity", "Payment method updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("OrderSummaryActivity", "Failed to update payment method", e)
                }
        } else {
            Log.e("OrderSummaryActivity", "Order ID or payment method is null")
        }
    }

    companion object {
        private const val PAYMENT_METHOD_REQUEST_CODE = 1
    }
}
