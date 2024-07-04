package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.R
import com.example.rormcustomer.models.Restaurant
import com.bumptech.glide.Glide

class SavedRestaurantAdapter(private val restaurantList: List<Restaurant>) :
    RecyclerView.Adapter<SavedRestaurantAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        val restaurantRatings: TextView = itemView.findViewById(R.id.restaurantRatings)
        val restaurantTag: TextView = itemView.findViewById(R.id.restaurantTag)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_restaurant, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.restaurantName.text = restaurant.name
        //holder.restaurantRatings.text = restaurant.ratings.toString()
        holder.restaurantTag.text = restaurant.cuisine

        // Load image with Glide
        Glide.with(holder.restaurantImage.context)
            .load(restaurant.images)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_placeholder)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .into(holder.restaurantImage)
    }

    override fun getItemCount() = restaurantList.size
}
