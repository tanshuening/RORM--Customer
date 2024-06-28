package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewPastRewardsBinding

class PastRewardsAdapter(
    private val pastRewards: List<String>,
    private val expiredDates: List<String>,
    private val rewardsStatus: List<String>,
    private val images: List<Int>
) : RecyclerView.Adapter<PastRewardsAdapter.PastRewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastRewardsViewHolder {
        val binding = CardViewPastRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PastRewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PastRewardsViewHolder, position: Int) {
        val pastReward = pastRewards[position]
        val expiredDate = expiredDates[position]
        val rewardsStatus = rewardsStatus[position]
        val image = images[position]
        holder.bind(pastReward, expiredDate, rewardsStatus, image)
    }

    override fun getItemCount(): Int {
        return pastRewards.size
    }

    class PastRewardsViewHolder(private val binding: CardViewPastRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, date: String, status: String, image: Int) {
            binding.pastRewardName.text = name
            binding.expiredDate.text = date
            binding.pastRewardStatus.text = status
            binding.pastRewardsImage.setImageResource(image)
        }
    }
}
