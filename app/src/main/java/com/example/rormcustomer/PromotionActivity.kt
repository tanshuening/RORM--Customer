package com.example.rormcustomer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.PromotionItemAdapter
import com.example.rormcustomer.databinding.ActivityPromotionBinding
import com.example.rormcustomer.models.PromotionItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PromotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPromotionBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var promotionQuery: Query
    private lateinit var restaurantRef: DatabaseReference
    private val promotions = mutableListOf<PromotionItem>()
    private lateinit var adapter: PromotionItemAdapter
    private var restaurantId: String? = null
    private var promotionName: String? = null
    private var promotionImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        restaurantId = intent.getStringExtra("restaurantId")
        if (restaurantId == null) {
            Log.e("PromotionActivity", "Restaurant ID is null")
            finish()
            return
        }

        adapter = PromotionItemAdapter(promotions, promotionName, promotionImage, { promotion ->
            val intent = Intent(this, MyRewardsInfoActivity::class.java).apply {
                putExtra("promotionName", promotion.name)
                putExtra("promotionDescription", promotion.description)
                putExtra("promotionTnc", promotion.termsAndConditions)
                putExtra("promotionDiscount", promotion.discount)
                putExtra("promotionStartDate", promotion.startDate)
                putExtra("promotionEndDate", promotion.endDate)
                putExtra("promotionImage", promotion.image)
            }
            startActivity(intent)
        }, { promotion, position ->
            showOfferSelectedDialog(promotion, position)
        })
        binding.rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rewardsRecyclerView.adapter = adapter

        binding.appBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        loadPromotions()
    }

    private fun showOfferSelectedDialog(promotion: PromotionItem, position: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Offer Selected")
            .setMessage("You have selected ${promotion.name}.")
            .setPositiveButton("Apply") { _, _ ->
                val viewHolder = binding.rewardsRecyclerView.findViewHolderForAdapterPosition(position) as? PromotionItemAdapter.PromotionViewHolder
                viewHolder?.binding?.addButton?.setImageResource(R.drawable.check_button)

                // Save promotion ID to the database
                val userId = auth.currentUser?.uid ?: return@setPositiveButton
                val orderRef = database.getReference("orders").orderByChild("userId").equalTo(userId)
                orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (orderSnapshot in snapshot.children) {
                            val restaurantIdFromDb = orderSnapshot.child("restaurantId").getValue(String::class.java)
                            if (restaurantIdFromDb == restaurantId) {
                                val orderId = orderSnapshot.key ?: continue
                                database.getReference("orders/$orderId/promotion").setValue(promotion.promotionId)
                                    .addOnSuccessListener {
                                        Log.d("ActiveRewardsActivity", "Promotion ID saved successfully")
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e("ActiveRewardsActivity", "Error saving promotion ID: ${error.message}")
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ActiveRewardsActivity", "Error saving promotion ID: ${error.message}")
                    }
                })

                // Save promotion name to SharedPreferences
                val sharedPref = getSharedPreferences("order_summary_prefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("promotionName", promotion.name)
                    apply()
                }

                // Navigate to OrderSummaryActivity with restaurantId, promotionName, promotionDiscount and booking details
                val intent = Intent(this@PromotionActivity, OrderSummaryActivity::class.java).apply {
                    putExtra("restaurantId", restaurantId)
                    putExtra("promotionId", promotion.promotionId)
                    putExtra("promotionName", promotion.name)
                    putExtra("promotionDiscount", promotion.discount)
/*                    putExtra("numOfPax", intent.getIntExtra("numOfPax", 0))
                    putExtra("bookingTime", intent.getStringExtra("bookingTime"))
                    putExtra("bookingDate", intent.getLongExtra("bookingDate", 0L))*/
                }
                startActivity(intent)
                finish() // Optional: finish the current activity if you don't want to return to it
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun loadPromotions() {
        val userId = auth.currentUser?.uid ?: return
        val currentDate = System.currentTimeMillis()
        promotionQuery = database.getReference("restaurants/$restaurantId/promotion")

        promotionQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                promotions.clear()
                for (promotionSnapshot in snapshot.children) {
                    val promotionItem = promotionSnapshot.getValue(PromotionItem::class.java)
                    promotionItem?.let {
                        try {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val startDate = dateFormat.parse(it.startDate)?.time ?: 0
                            val endDate = dateFormat.parse(it.endDate)?.time ?: 0
                            if (currentDate in startDate..endDate) {
                                promotions.add(it)
                            } else {

                            }
                        } catch (e: ParseException) {
                            Log.e("ActiveRewardsActivity", "Error parsing date: ${e.message}")
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ActiveRewardsActivity", "Error loading promotions: ${error.message}")
            }
        })
    }
}