package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation

class PastReservationAdapter(private val pastReservations: List<Reservation>) : RecyclerView.Adapter<PastReservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardViewUpcomingReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = pastReservations[position]
        holder.bind(reservation)
    }

    override fun getItemCount(): Int {
        return pastReservations.size
    }

    class ViewHolder(private val binding: CardViewUpcomingReservationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reservation: Reservation) {
            binding.restaurantName.text = reservation.restaurant?.name
            //binding.restaurantRatings.text = reservation.restaurant?.cuisine // Assuming you have ratings in restaurant model
            // Glide is used to load images from URLs
            Glide.with(binding.restaurantImage.context)
                .load(reservation.restaurant?.images?.get(0)) // Assuming the first image is the main image
                .into(binding.restaurantImage)
        }
    }
}
