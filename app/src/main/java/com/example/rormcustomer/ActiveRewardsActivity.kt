package com.example.rormcustomer

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.ActiveRewardsAdapter
import com.example.rormcustomer.databinding.ActivityActiveRewardsBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ActiveRewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveRewardsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var promotionsQuery: Query
    private val promotionNames = mutableListOf<String>()
    private val promotionEndDates = mutableListOf<String>()
    private val promotionImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        promotionsQuery = database.getReference("restaurants").child("-O0EyC4lJG3bf5EJgliM").child("promotion")

        binding.rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rewardsRecyclerView.adapter = ActiveRewardsAdapter(promotionNames, promotionEndDates, promotionImages)

        loadPromotions()

        binding.rewardsAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadPromotions() {
        promotionsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                promotionNames.clear()
                promotionEndDates.clear()
                promotionImages.clear()

                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val currentDateParsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(currentDate)

                for (promotionSnapshot in snapshot.children) {
                    val endDate = promotionSnapshot.child("endDate").getValue(String::class.java)
                    val endDateParsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDate)
                    if (endDateParsed != null && endDateParsed.after(currentDateParsed)) {
                        val name = promotionSnapshot.child("name").getValue(String::class.java)
                        val image = promotionSnapshot.child("image").getValue(String::class.java)

                        name?.let { promotionNames.add(it) }
                        endDate?.let { promotionEndDates.add(it) }
                        image?.let { promotionImages.add(it) }
                    }
                }

                binding.rewardsRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ActiveRewardsActivity", "Error loading promotions: ${error.message}")
            }
        })
    }
}
