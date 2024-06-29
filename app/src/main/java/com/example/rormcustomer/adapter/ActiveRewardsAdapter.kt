package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewActiveRewardsBinding
import com.example.rormcustomer.models.PromotionItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActiveRewardsAdapter(
    private val promotions: List<PromotionItem>,
    private var promotionName: String?,
    private var promotionImage: String?,
    private val onPromotionClick: (PromotionItem) -> Unit
) : RecyclerView.Adapter<ActiveRewardsAdapter.ActiveRewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveRewardsViewHolder {
        val binding = CardViewActiveRewardsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActiveRewardsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActiveRewardsViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.bind(promotion, promotionName, promotionImage)
        holder.itemView.setOnClickListener {
            onPromotionClick(promotion)
        }
    }

    override fun getItemCount(): Int {
        return promotions.size
    }

    fun updatePromotionDetails(name: String?, image: String?) {
        promotionName = name
        promotionImage = image
        notifyDataSetChanged()
    }

    class ActiveRewardsViewHolder(private val binding: CardViewActiveRewardsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(promotion: PromotionItem, promotionName: String?, image: String?) {
            binding.promotionName.text = promotionName ?: promotion.name
            binding.promotionEndDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(promotion.endDate)
            )

            val imageUrl = image ?: promotion.image
            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.promotionImage)
            }
        }
    }
}
