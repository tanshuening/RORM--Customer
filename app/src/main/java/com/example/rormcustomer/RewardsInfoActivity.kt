package com.example.rormcustomer

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

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveAndDisplayRewardsItem() {
        rewardsItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val rewardsItem = snapshot.getValue(Rewards::class.java)
                    if (rewardsItem != null) {
                        displayRewardsItem(rewardsItem)
                        Log.d("RewardsInfoActivity", "Reward retrieved successfully: $rewardsItem")
                    } else {
                        Log.e("RewardsInfoActivity", "Reward is null")
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

    private fun displayRewardsItem(rewards: Rewards) {
        binding.apply {
            promotionNameTextView.text = rewards.name
            promotionStartDateTextView.text = rewards.startDate
            promotionEndDateTextView.text = rewards.endDate
            promotionDescriptionTextView.text = rewards.description

            val imageUri = Uri.parse(rewards.image)
            Glide.with(this@RewardsInfoActivity).load(imageUri).into(promotionImageView)
        }
    }

    companion object {
        const val REWARDS_ITEM_ID = "rewardsItemId"
        const val RESTAURANT_ID = "restaurantId"
    }
}
