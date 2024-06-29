package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewActiveRewardsBinding

class ActiveRewardsAdapter(
    private val promotionNames: List<String>,
    private val promotionEndDates: List<String>,
    private val promotionImages: List<String>
) : RecyclerView.Adapter<ActiveRewardsAdapter.ActiveRewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveRewardsViewHolder {
        val binding = CardViewActiveRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiveRewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActiveRewardsViewHolder, position: Int) {
        val promotionName = promotionNames[position]
        val promotionEndDate = promotionEndDates[position]
        val promotionImage = promotionImages[position]
        holder.bind(promotionName, promotionEndDate, promotionImage)
    }

    override fun getItemCount(): Int {
        return promotionNames.size
    }

    class ActiveRewardsViewHolder(private val binding: CardViewActiveRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, date: String, image: String) {
            binding.promotionName.text = name
            binding.promotionEndDate.text = date
            Glide.with(binding.promotionImage.context).load(image).into(binding.promotionImage)
        }
    }
}
