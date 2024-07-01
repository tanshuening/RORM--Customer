package com.example.rormcustomer

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.databinding.ActivityRewardsBinding
import com.examples.rorm_fyp.adapter.RewardsAdapter

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
/*
        val rewardName = listOf("RM5 off", "RM10 off", "10% off")
        val loyaltyPoints = listOf("200", "300", "100")
        val rewardImage = listOf(
            R.drawable.reward,
            R.drawable.reward,
            R.drawable.reward
        )

        val adapter = RewardsAdapter(ArrayList(rewardName), ArrayList(loyaltyPoints), ArrayList(rewardImage))
        binding.rewardsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rewardsRecyclerView.adapter = adapter

        binding.rewardsAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }*/
    }
}
