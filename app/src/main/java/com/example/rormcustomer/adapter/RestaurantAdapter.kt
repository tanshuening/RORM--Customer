package com.example.rormcustomer.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.R
import com.example.rormcustomer.RestaurantInfoActivity
import com.example.rormcustomer.databinding.CardViewRestaurantBinding
import com.example.rormcustomer.models.Restaurant

class RestaurantAdapter(
    private val restaurantItems: List<Restaurant>,
    private val requireContext: Context
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding =
            CardViewRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = restaurantItems.size

    inner class RestaurantViewHolder(private val binding: CardViewRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val restaurantItem = restaurantItems[position]

            binding.apply {
                restaurantName.text = restaurantItem.name
                restaurantTag.text = restaurantItem.cuisine

                if (!restaurantItem.images.isNullOrEmpty()) {
                    val uri = Uri.parse(restaurantItem.images[0])
                    Glide.with(requireContext).load(uri).into(restaurantImage)
                } else {
                    restaurantImage.setImageResource(com.denzcoskun.imageslider.R.drawable.default_placeholder)
                }

                root.setOnClickListener {
                    openDetailsActivity(restaurantItem)
                }
            }
        }

        private fun openDetailsActivity(restaurantItem: Restaurant) {
            val intent = Intent(requireContext, RestaurantInfoActivity::class.java).apply {
                putExtra("RestaurantId", restaurantItem.restaurantId)
                putExtra("RestaurantName", restaurantItem.name)
                putExtra("RestaurantCuisine", restaurantItem.cuisine)
                putExtra("RestaurantLocation", restaurantItem.location)
                putExtra("RestaurantAddress", restaurantItem.address)
                putExtra("RestaurantPrice", restaurantItem.price)
                putExtra("RestaurantPayment", restaurantItem.payment)
                putExtra("RestaurantParking", restaurantItem.parking)
                putExtra("RestaurantDressCode", restaurantItem.dressCode)
                putExtra("RestaurantDescription", restaurantItem.description)
                putExtra("RestaurantPhoneNumber", restaurantItem.phone)
                putExtra("RestaurantImage", restaurantItem.images?.get(0))
                putExtra("RestaurantBusinessStartTime", restaurantItem.startTime)
                putExtra("RestaurantBusinessEndTime", restaurantItem.endTime)
            }
            Log.d("RestaurantAdapter", "Starting RestaurantInfoActivity with RestaurantId: ${restaurantItem.restaurantId}")
            requireContext.startActivity(intent)
        }
    }
}
