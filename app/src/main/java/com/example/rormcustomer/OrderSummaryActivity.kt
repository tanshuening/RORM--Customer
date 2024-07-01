package com.example.rormcustomer

import android.app.Activity
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
    private var reservationId: String? = null
    private var subtotal: Double = 0.0
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase and Authentication instances
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Retrieve promotion discount, restaurant ID, and reservation ID from intent
        promotionDiscount = intent.getStringExtra("promotionDiscount")?.toDoubleOrNull()
        restaurantId = intent.getStringExtra("restaurantId")
        reservationId = intent.getStringExtra("reservationId")

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

        // Initialize Firebase Database references
        orderQuery = database.getReference("orders").orderByChild("userId").equalTo(userId)
        restaurantRef = database.getReference("restaurants").child(restaurantId!!)

        loadOrderData()
        loadRestaurantName()
    }

    // Function to set up reservation details
    private fun setupReservationDetails() {
        val numOfPax = intent.getIntExtra("numOfPax", 0)
        val bookingDate = intent.getLongExtra("bookingDate", 0L)
        val bookingTime = intent.getStringExtra("bookingTime")

        if (numOfPax != 0 && bookingDate != 0L && bookingTime != null) {
            val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(bookingDate))
            binding.reservationText.text = "$numOfPax pax, $dateFormatted, $bookingTime"
        }
    }

    // Function to set up RecyclerView for order summary items
    private fun setupRecyclerView() {
        adapter = OrderSummaryMenuItemAdapter(items, prices, quantities, restaurantId!!)
        binding.orderSummaryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.orderSummaryRecyclerView.adapter = adapter
    }

    // Function to set up click listeners for various UI elements
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
            startActivityForResult(intent, RESERVATION_REQUEST_CODE)
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

        binding.orderButton.setOnClickListener {
            saveOrderToDatabase()
        }
    }

    // Function to load saved promotion name from shared preferences
    private fun loadSavedPromotionName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val savedPromotionName = sharedPref.getString("promotionName", null)
        savedPromotionName?.let {
            binding.promotionsText.text = it
        }
    }

    // Function to restore payment method from savedInstanceState or database
    private fun restorePaymentMethod(savedInstanceState: Bundle?) {
        paymentMethod = savedInstanceState?.getString("paymentMethod") ?: loadPaymentMethodFromDatabase()
        binding.paymentMethodText.text = paymentMethod ?: "Payment Method"
    }

    // Function to load order data from Firebase
    private fun loadOrderData() {
        orderQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                prices.clear()
                quantities.clear()

                for (orderSnapshot in snapshot.children) {
                    val restaurantIdFromDb = orderSnapshot.child("restaurantId").getValue(String::class.java)
                    if (restaurantIdFromDb == restaurantId) {
                        orderId = orderSnapshot.key
                        val itemsSnapshot = orderSnapshot.child("orderItems")
                        for (itemSnapshot in itemsSnapshot.children) {
                            itemSnapshot.child("itemName").getValue(String::class.java)?.let { items.add(it) }
                            itemSnapshot.child("price").getValue(Double::class.java)?.let { prices.add(it) }
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

    // Function to calculate subtotal and total amount
    private fun calculateTotals() {
        subtotal = prices.zip(quantities).sumOf { it.first * it.second }
        binding.subtotalAmount.text = String.format("%.2f", subtotal)

        totalAmount = promotionDiscount?.let { discount ->
            subtotal - (subtotal * discount / 100)
        } ?: subtotal

        binding.totalAmount.text = String.format("%.2f", totalAmount)
    }

    // Function to load restaurant name from Firebase
    private fun loadRestaurantName() {
        restaurantRef.child("name").get().addOnSuccessListener { snapshot ->
            snapshot.getValue(String::class.java)?.let {
                binding.restaurantName.text = it
            }
        }.addOnFailureListener {
            Log.e("OrderSummaryActivity", "Failed to load restaurant name", it)
        }
    }

    // Function to load payment method from database
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

    // Function to save order to Firebase database
    private fun saveOrderToDatabase() {
        val userId = auth.currentUser?.uid ?: return
        val ordersRef = database.getReference("orders")
        val orderData = mutableMapOf<String, Any>(
            "userId" to userId,
            "restaurantId" to restaurantId!!,
            "subtotal" to subtotal,
            "totalAmount" to totalAmount,
            "orderItems" to items.mapIndexed { index, item ->
                mapOf(
                    "itemName" to item,
                    "price" to prices[index],
                    "quantity" to quantities[index]
                )
            },
            "paymentMethod" to paymentMethod.orEmpty(),
            "promotionDiscount" to (promotionDiscount ?: 0.0),
            "reservationId" to reservationId.orEmpty()
        )

        orderId?.let { id ->
            ordersRef.child(id).setValue(orderData).addOnSuccessListener {
                Log.d("OrderSummaryActivity", "Order updated successfully")
                clearOrderSummary()
            }.addOnFailureListener {
                Log.e("OrderSummaryActivity", "Failed to update order", it)
            }
        } ?: run {
            orderId = ordersRef.push().key
            ordersRef.child(orderId!!).setValue(orderData).addOnSuccessListener {
                Log.d("OrderSummaryActivity", "Order created successfully")
                clearOrderSummary()
            }.addOnFailureListener {
                Log.e("OrderSummaryActivity", "Failed to create order", it)
            }
        }
    }

    // Function to clear order summary after saving the order
    private fun clearOrderSummary() {
        items.clear()
        prices.clear()
        quantities.clear()
        adapter.notifyDataSetChanged()
        binding.subtotalAmount.text = "0.00"
        binding.totalAmount.text = "0.00"
        binding.paymentMethodText.text = "Payment Method"
        binding.promotionsText.text = "Promotions"
        orderId = null
        paymentMethod = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("paymentMethod", paymentMethod)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PAYMENT_METHOD_REQUEST_CODE -> {
                    paymentMethod = data?.getStringExtra("selectedPaymentMethod")
                    binding.paymentMethodText.text = paymentMethod
                }
                RESERVATION_REQUEST_CODE -> {
                    reservationId = data?.getStringExtra("selectedReservationId")
                    val numOfPax = data?.getIntExtra("numOfPax", 0) ?: 0
                    val bookingDate = data?.getLongExtra("bookingDate", 0L) ?: 0L
                    val bookingTime = data?.getStringExtra("bookingTime")

                    if (numOfPax != 0 && bookingDate != 0L && bookingTime != null) {
                        val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(bookingDate))
                        binding.reservationText.text = "$numOfPax pax, $dateFormatted, $bookingTime"
                    }
                }
            }
        }
    }

    companion object {
        private const val PAYMENT_METHOD_REQUEST_CODE = 1
        private const val RESERVATION_REQUEST_CODE = 2
    }
}
