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
    private lateinit var redeemedAdapter: RewardsAdapter
    private var rewardsItems: MutableList<Rewards> = mutableListOf()
    private var redeemedRewards: MutableList<Rewards> = mutableListOf()
    private var loyaltyPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        binding.rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RewardsAdapter(this, rewardsItems, object : RewardsAdapter.OnItemClickListener {
            override fun onItemClick(rewards: Rewards, restaurantId: String) {
                navigateToRewardsInfo(rewards.rewardsId, restaurantId)
            }
        })
        binding.rewardsRecyclerView.adapter = adapter

        binding.myRewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        redeemedAdapter = RewardsAdapter(this, redeemedRewards, object : RewardsAdapter.OnItemClickListener {
            override fun onItemClick(rewards: Rewards, restaurantId: String) {
                navigateToRewardsInfo(rewards.rewardsId, restaurantId)
            }
        })
        binding.myRewardsRecyclerView.adapter = redeemedAdapter

        retrieveLoyaltyPoints()
        retrieveRewardsItem()
        retrieveRedeemedRewards()

        binding.toolbar.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun navigateToRewardsInfo(rewardsItemId: String, restaurantId: String) {
        val intent = Intent(this@RewardsActivity, RewardsInfoActivity::class.java).apply {
            putExtra(RewardsInfoActivity.REWARDS_ITEM_ID, rewardsItemId)
            putExtra(RewardsInfoActivity.RESTAURANT_ID, restaurantId)
        }
        startActivityForResult(intent, REWARDS_INFO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REWARDS_INFO_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getIntExtra("newPoints", loyaltyPoints)?.let {
                updateLoyaltyPoints(FirebaseAuth.getInstance().currentUser?.uid ?: return, it)
            }
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
                Log.e("RewardsActivity", "Failed to retrieve reservations", error.toException())
            }
        })
    }

    private fun updateLoyaltyPoints(userId: String, points: Int) {
        loyaltyPoints = points
        val userRef = database.reference.child("users").child(userId).child("loyaltyPoints")
        userRef.setValue(points)
        updateLoyaltyTier(points)
    }

    private fun updateLoyaltyTier(loyaltyPoints: Int) {
        val loyaltyTier = when {
            loyaltyPoints >= 1000 -> "Diamond"
            loyaltyPoints >= 500 -> "Gold"
            loyaltyPoints >= 100 -> "Silver"
            else -> "Bronze"
        }
        binding.loyaltyTier.text = loyaltyTier
        binding.loyaltyPoints.text = loyaltyPoints.toString()
    }

    private fun retrieveRewardsItem() {
        val rewardsRef = database.reference.child("rewards")
        rewardsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewardsItems.clear()
                for (rewardSnapshot in snapshot.children) {
                    val reward = rewardSnapshot.getValue(Rewards::class.java)
                    reward?.let { rewardsItems.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RewardsActivity", "Failed to retrieve rewards", error.toException())
            }
        })
    }

    private fun retrieveRedeemedRewards() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val redeemedRewardsRef = database.reference.child("users").child(userId).child("redeemedRewards")

        redeemedRewardsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                redeemedRewards.clear()
                for (redeemedRewardSnapshot in snapshot.children) {
                    val redeemedReward = redeemedRewardSnapshot.getValue(Rewards::class.java)
                    redeemedReward?.let { redeemedRewards.add(it) }
                }
                redeemedAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RewardsActivity", "Failed to retrieve redeemed rewards", error.toException())
            }
        })
    }

    companion object {
        const val REWARDS_INFO_REQUEST_CODE = 1001
    }
}
