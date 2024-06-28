package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewActiveRewardsBinding

class ActiveRewardsAdapter(
    private val activeRewards: List<String>,
    private val expiredDates: List<String>,
    private val images: List<Int>
) : RecyclerView.Adapter<ActiveRewardsAdapter.ActiveRewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveRewardsViewHolder {
        val binding = CardViewActiveRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiveRewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActiveRewardsViewHolder, position: Int) {
        val activeReward = activeRewards[position]
        val expiredDate = expiredDates[position]
        val image = images[position]
        holder.bind(activeReward, expiredDate, image)
    }

    override fun getItemCount(): Int {
        return activeRewards.size
    }

    class ActiveRewardsViewHolder(private val binding: CardViewActiveRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, date: String, image: Int) {
            binding.activeRewardName.text = name
            binding.expiredDate.text = date
            binding.activeRewardsImage.setImageResource(image)
        }
    }
}
