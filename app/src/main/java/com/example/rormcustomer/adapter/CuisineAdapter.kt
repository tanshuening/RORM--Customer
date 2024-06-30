package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewCuisineBinding

class CuisineAdapter(
    private val cuisines: List<String>,
    private val images: List<Int>
) : RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val binding = CardViewCuisineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        val cuisine = cuisines[position]
        val image = images[position]
        holder.bind(cuisine, image)
    }

    override fun getItemCount(): Int {
        return cuisines.size
    }

    inner class CuisineViewHolder(private val binding: CardViewCuisineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cuisine: String, image: Int) {
            binding.cuisineName.text = cuisine
            binding.restaurantImage.setImageResource(image)
            // Add click listener if needed
        }
    }
}
