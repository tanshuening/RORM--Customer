package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityRewardsInfoBinding

class RewardsInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up the back button
        binding.rewardsInfoAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, RewardsActivity::class.java)
            startActivity(intent)
        }

        // Setting the data
        val rewardName = "Sample Reward"
        val image = R.drawable.voucher2
        val points = "1500"
        val startDate = "2024-01-01"
        val endDate = "2024-12-31"
        val description = "This is a sample reward description."
        val tnc = "These are the terms and conditions."

        binding.rewardName.text = rewardName
        binding.rewardsImage.setImageResource(image)
        binding.loyaltyPoints.text = points
        binding.validityStartDate.text = startDate
        binding.validityEndDate.text = endDate
        binding.description.text = description
        binding.tnc.text = tnc
    }
}
