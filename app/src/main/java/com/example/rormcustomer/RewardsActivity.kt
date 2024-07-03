package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.RewardsAdapter
import com.example.rormcustomer.databinding.ActivityRewardsBinding
import com.example.rormcustomer.models.Rewards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RewardsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRewardsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: RewardsAdapter
    private var rewardsItems: MutableList<Rewards> = mutableListOf()
    private var loyaltyPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        binding.rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RewardsAdapter(this, rewardsItems, object : RewardsAdapter.OnItemClickListener {
            override fun onItemClick(rewards: Rewards, restaurantId: String) {
                val intent = Intent(this@RewardsActivity, RewardsInfoActivity::class.java).apply {
                    putExtra(RewardsInfoActivity.REWARDS_ITEM_ID, rewards.rewardsId)
                    putExtra(RewardsInfoActivity.RESTAURANT_ID, restaurantId)
                }
                startActivity(intent)
            }
        })
        binding.rewardsRecyclerView.adapter = adapter

        retrieveLoyaltyPoints()
        retrieveRewardsItem()

        binding.toolbar.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun retrieveLoyaltyPoints() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val reservationsRef = database.reference.child("reservations")

        reservationsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loyaltyPoints = 0
                for (reservationSnapshot in snapshot.children) {
                    loyaltyPoints += 100
                }
                updateLoyaltyPoints(userId, loyaltyPoints)
                updateLoyaltyTier(loyaltyPoints)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Failed to retrieve reservations: ${error.message}")
            }
        })
    }

    private fun updateLoyaltyPoints(userId: String, points: Int) {
        val userRef = database.reference.child("users").child(userId).child("loyaltyPoints")
        userRef.setValue(points)
    }

    private fun updateLoyaltyTier(points: Int) {
        val tier = when {
            points >= 1000 -> "Platinum"
            points >= 500 -> "Gold"
            points >= 200 -> "Silver"
            else -> "Bronze"
        }
        binding.loyaltyPoints.text = points.toString()
        binding.loyaltyTier.text = tier
    }

    private fun retrieveRewardsItem() {
        val restaurantsRef = database.reference.child("restaurants")

        restaurantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardsItems.clear()
                for (restaurantSnapshot in snapshot.children) {
                    val restaurantId = restaurantSnapshot.key ?: continue
                    val rewardsRef: DatabaseReference = database.reference.child("restaurants").child(restaurantId).child("rewards")

                    rewardsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(rewardsSnapshot: DataSnapshot) {
                            for (rewardsItemSnapshot in rewardsSnapshot.children) {
                                val rewardsItem = rewardsItemSnapshot.getValue(Rewards::class.java)
                                rewardsItem?.let {
                                    it.rewardsId = rewardsItemSnapshot.key.toString()
                                    if (!rewardsItems.contains(it)) {
                                        rewardsItems.add(it)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("DatabaseError", "Failed to retrieve reward items: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Failed to retrieve restaurants: ${error.message}")
            }
        })
    }
}
