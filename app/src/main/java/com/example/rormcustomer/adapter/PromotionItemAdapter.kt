package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewPromotionBinding
import com.example.rormcustomer.models.PromotionItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PromotionItemAdapter(
    private val promotions: List<PromotionItem>,
    private var promotionName: String?,
    private var promotionImage: String?,
    private val onPromotionClick: (PromotionItem) -> Unit,
    private val onAddButtonClick: (PromotionItem, Int) -> Unit
) : RecyclerView.Adapter<PromotionItemAdapter.PromotionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = CardViewPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionViewHolder(binding, onAddButtonClick)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.bind(promotion, promotionName, promotionImage)
        holder.itemView.setOnClickListener {
            onPromotionClick(promotion)
        }
    }

    override fun getItemCount(): Int {
        return promotions.size
    }

    class PromotionViewHolder(
        val binding: CardViewPromotionBinding,
        private val onAddButtonClick: (PromotionItem, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

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

            binding.addButton.setOnClickListener {
                onAddButtonClick(promotion, adapterPosition)
            }
        }
    }
}