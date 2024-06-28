package com.examples.rorm_fyp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewRewardsBinding

class RewardsAdapter(
    private val rewards: List<String>,
    private val loyaltyPoints: List<String>,
    private val images: List<Int>
) : RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsViewHolder {
        val binding = CardViewRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardsViewHolder, position: Int) {
        val reward = rewards[position]
        val loyaltyPoint = loyaltyPoints[position]
        val image = images[position]
        holder.bind(reward, loyaltyPoint, image)
    }

    override fun getItemCount(): Int {
        return rewards.size
    }

    class RewardsViewHolder(private val binding: CardViewRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, points: String, image: Int) {
            binding.rewardName.text = name
            binding.loyaltyPoints.text = points
            binding.rewardsImage.setImageResource(image)
        }
    }
}
