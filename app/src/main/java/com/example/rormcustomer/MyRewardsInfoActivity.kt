package com.example.rormcustomer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.ActivityMyRewardsInfoBinding

class MyRewardsInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRewardsInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRewardsInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Retrieve data from Intent
        val promotionName = intent.getStringExtra("promotionName")
        val promotionDescription = intent.getStringExtra("promotionDescription")
        val promotionTnc = intent.getStringExtra("promotionTnc")
        val promotionDiscount = intent.getStringExtra("promotionDiscount")
        val promotionStartDate = intent.getStringExtra("promotionStartDate")
        val promotionEndDate = intent.getStringExtra("promotionEndDate")
        val promotionImage = intent.getStringExtra("promotionImage")

        // Set data to views
        binding.myRewardName.text = promotionName
        binding.loyaltyPoints.text = promotionDiscount
        binding.validityStartDate.text = promotionStartDate
        binding.validityEndDate.text = promotionEndDate
        binding.description.text = promotionDescription
        binding.tnc.text = promotionTnc

        // Load the image using Glide
        if (!promotionImage.isNullOrEmpty()) {
            Glide.with(this).load(promotionImage).into(binding.myRewardsImage)
        }
    }
}