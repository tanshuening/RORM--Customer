package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rormcustomer.adapter.ActiveRewardsAdapter
import com.example.rormcustomer.adapter.UpcomingReservationAdapter
import com.example.rormcustomer.databinding.ActivityActiveRewardsBinding
import com.example.rormcustomer.databinding.ActivityMyRewardsBinding
import com.example.rormcustomer.databinding.ActivityRewardsBinding
import com.example.rormcustomer.databinding.ActivityUpcomingReservationBinding
import com.example.rormcustomer.models.PromotionItem
import com.example.rormcustomer.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ActiveRewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveRewardsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var promotionQuery: Query
    private lateinit var restaurantRef: DatabaseReference
    private val promotions = mutableListOf<PromotionItem>()
    private lateinit var adapter: ActiveRewardsAdapter
    private var restaurantId: String? = null
    private var promotionName: String? = null
    private var promotionImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        restaurantId = intent.getStringExtra("restaurantId")
        if (restaurantId == null) {
            Log.e("ActiveRewardsActivity", "Restaurant ID is null")
            finish()
            return
        }

        adapter = ActiveRewardsAdapter(promotions, promotionName, promotionImage, { promotion ->
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
            onBackPressed()
        }

        loadPromotions()
    }

    private fun showOfferSelectedDialog(promotion: PromotionItem, position: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Offer Selected")
            .setMessage("You have selected ${promotion.name}.")
            .setPositiveButton("Apply") { _, _ ->
                val viewHolder = binding.rewardsRecyclerView.findViewHolderForAdapterPosition(position) as? ActiveRewardsAdapter.ActiveRewardsViewHolder
                viewHolder?.binding?.addButton?.setImageResource(R.drawable.check_button)

                // Navigate to OrderSummaryActivity with restaurantId and promotionName
                val intent = Intent(this, OrderSummaryActivity::class.java).apply {
                    putExtra("restaurantId", restaurantId)
                    putExtra("promotionName", promotion.name)
                }
                startActivity(intent)
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