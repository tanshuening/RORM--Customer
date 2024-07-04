package com.example.rormcustomer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.ActivityRewardsInfoBinding
import com.example.rormcustomer.models.Rewards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RewardsInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsInfoBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var rewardsItemRef: DatabaseReference
    private var rewardsItemId: String? = null
    private var restaurantId: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUrl: String
    private lateinit var redeemedRewardsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        rewardsItemId = intent.getStringExtra(REWARDS_ITEM_ID)
        restaurantId = intent.getStringExtra(RESTAURANT_ID)

        if (rewardsItemId != null && restaurantId != null) {
            rewardsItemRef = database.getReference("restaurants")
                .child(restaurantId!!)
                .child("rewards")
                .child(rewardsItemId!!)
            retrieveAndDisplayRewardsItem()
        } else {
            Log.e("RewardsInfoActivity", "RewardsItemId or restaurantId is null")
            finish()
        }

        redeemedRewardsRef = database.reference
            .child("users")
            .child(auth.currentUser!!.uid)
            .child("redeemedRewards")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.redeemButton.setOnClickListener {
            redeemReward()
        }
    }

    private fun retrieveAndDisplayRewardsItem() {
        rewardsItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val rewardsItem = snapshot.getValue(Rewards::class.java)
                        if (rewardsItem != null) {
                            displayRewardsItem(rewardsItem)
                            Log.d(
                                "RewardsInfoActivity",
                                "Reward retrieved successfully: $rewardsItem"
                            )
                        } else {
                            Log.e("RewardsInfoActivity", "Reward is null")
                        }
                    } catch (e: DatabaseException) {
                        Log.e("RewardsInfoActivity", "Failed to convert value: ${e.message}", e)
                    }
                } else {
                    Log.e("RewardsInfoActivity", "Reward does not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RewardsInfoActivity", "Failed to retrieve rewards", error.toException())
            }
        })
    }

    private fun displayRewardsItem(rewardsItem: Rewards) {
        with(binding) {
            promotionNameTextView.text = rewardsItem.name
            promotionDescriptionTextView.text = rewardsItem.description
            promotionPointsTextView.text = rewardsItem.points.toString()
            promotionTncTextView.text = rewardsItem.termsAndConditions
            promotionStartDateTextView.text = rewardsItem.startDate
            promotionEndDateTextView.text = rewardsItem.endDate
            imageUrl = rewardsItem.image
            Glide.with(this@RewardsInfoActivity).load(Uri.parse(imageUrl))
                .into(promotionImageView)
        }
    }

    private fun redeemReward() {
        rewardsItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rewardsItem = snapshot.getValue(Rewards::class.java)
                if (rewardsItem != null) {
                    redeemedRewardsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.hasChild(rewardsItemId!!)) {
                                val currentPoints = dataSnapshot.getValue(Int::class.java) ?: 0
                                val requiredPoints = rewardsItem.points ?: 0 // Handle nullable points

                                if (currentPoints >= requiredPoints) {
                                    val newPoints = currentPoints - requiredPoints
                                    redeemedRewardsRef.child(rewardsItemId!!).setValue(rewardsItem)
                                    updateLoyaltyPoints(newPoints)
                                    updateLoyaltyPointsInRewardsActivity(newPoints) // Call this method to update points in RewardsActivity
                                    markRewardAsRedeemed()
                                } else {
                                    Log.e("RewardsInfoActivity", "Not enough points to redeem reward")
                                }
                            } else {
                                Log.e("RewardsInfoActivity", "Reward already redeemed by the user")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RewardsInfoActivity", "Failed to retrieve redeemed rewards", error.toException())
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RewardsInfoActivity", "Failed to retrieve rewards", error.toException())
            }
        })
    }

    private fun updateLoyaltyPoints(points: Int) {
        val userRef = database.reference.child("users").child(auth.currentUser!!.uid)
        userRef.child("loyaltyPoints").setValue(points)
    }

    private fun markRewardAsRedeemed() {
        rewardsItemRef.child("redeemed").setValue(true)
        binding.redeemButton.isEnabled = false
        binding.redeemButton.text = "Redeemed"
    }

    private fun updateLoyaltyPointsInRewardsActivity(newPoints: Int) {
        val intent = Intent().apply {
            putExtra("newPoints", newPoints)
        }
        setResult(RESULT_OK, intent)
    }

    companion object {
        const val REWARDS_ITEM_ID = "rewardsItemId"
        const val RESTAURANT_ID = "restaurantId"
    }
}
