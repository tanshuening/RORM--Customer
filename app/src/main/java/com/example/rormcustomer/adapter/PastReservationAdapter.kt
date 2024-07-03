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
            // Load restaurant image using Glide
            reservation.restaurant?.images?.firstOrNull()?.let { imageUrl ->
                Glide.with(binding.restaurantImage.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(binding.restaurantImage)
            }
        }
    }
}
