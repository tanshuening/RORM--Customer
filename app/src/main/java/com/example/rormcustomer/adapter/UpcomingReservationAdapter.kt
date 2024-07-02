package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewReservationsBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant

class UpcomingReservationAdapter(
    private val reservations: List<Reservation>,
    private val restaurants: Map<String, Restaurant>,
    private val onItemClick: (Reservation) -> Unit,
    private val onItemSelect: (Reservation, Int) -> Unit
) : RecyclerView.Adapter<UpcomingReservationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CardViewReservationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reservation: Reservation) {
            val restaurant = restaurants[reservation.restaurantId]
            if (restaurant != null) {
                binding.restaurantName.text = restaurant.name
                //binding.restaurantRatings.text = restaurant.ratings.toString()
                binding.restaurantTag.text = restaurant.cuisine
                Glide.with(binding.root.context).load(restaurant.images).into(binding.restaurantImage)
            }
/*
            binding.numOfPax.text = "Pax: ${reservation.numOfPax}"
            binding.date.text = "Date: ${reservation.date}"
            binding.time.text = "Time: ${reservation.time}"
*/

            binding.root.setOnClickListener {
                onItemClick(reservation)
            }

            binding.root.setOnLongClickListener {
                onItemSelect(reservation, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardViewReservationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reservations[position])
    }

    override fun getItemCount(): Int = reservations.size
}
