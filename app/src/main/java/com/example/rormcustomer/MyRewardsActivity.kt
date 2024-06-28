package com.example.rormcustomer

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.adapter.MyRewardsPagerAdapter
import com.example.rormcustomer.databinding.ActivityMyRewardsBinding
import com.google.android.material.tabs.TabLayoutMediator

class MyRewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myRewardsAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = MyRewardsPagerAdapter(this)
        adapter.addFragment(ActiveRewardsFragment(), "Active Rewards")
        adapter.addFragment(PastRewardsFragment(), "Past Rewards")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}
