package com.example.rormcustomer.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewRewardsBinding
import com.example.rormcustomer.models.Rewards

class RewardsAdapter(
    private val context: Context,
    private val rewardsList: MutableList<Rewards>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder>() {

    inner class RewardsViewHolder(private val binding: CardViewRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rewardsItem: Rewards, restaurantId: String) {
            binding.apply {
                val uriString = rewardsItem.image
                val uri = Uri.parse(uriString)

                promotionName.text = rewardsItem.name
                expiredDate.text = rewardsItem.endDate

                Glide.with(context).load(uri).into(promotionImage)

                root.setOnClickListener {
                    listener.onItemClick(rewardsItem, restaurantId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsViewHolder {
        val binding = CardViewRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardsViewHolder, position: Int) {
        val rewardsItem = rewardsList[position]
        val restaurantId = rewardsItem.restaurantId
        holder.bind(rewardsItem, restaurantId)
    }

    override fun getItemCount(): Int {
        return rewardsList.size
    }

    interface OnItemClickListener {
        fun onItemClick(rewards: Rewards, restaurantId: String)
    }
}
