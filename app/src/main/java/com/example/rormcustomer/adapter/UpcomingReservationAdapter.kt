package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.R
import com.example.rormcustomer.databinding.CardViewUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import java.text.SimpleDateFormat
import java.util.*

class UpcomingReservationAdapter(
    private val reservations: List<Reservation>,
    private val restaurant: Restaurant?,
    private val onItemClick: (Reservation) -> Unit,
    private val onItemLongClick: (Reservation, Int) -> Unit
) : RecyclerView.Adapter<UpcomingReservationAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = CardViewUpcomingReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation, restaurant)
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class ReservationViewHolder(private val binding: CardViewUpcomingReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation, restaurant: Restaurant?) {
            binding.restaurantName.text = restaurant?.name
            binding.bookingNumOfPax.text = reservation.numOfPax.toString()
            binding.bookingDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(reservation.date))
            binding.bookingTime.text = reservation.timeSlot

            val imageUrl = restaurant?.images?.firstOrNull()
            imageUrl?.let { url ->
                Glide.with(binding.root.context)
                    .load(url)
                    .into(binding.restaurantImage)
            }

            binding.addButton.setOnClickListener {
                onItemClick(reservation)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(reservation, adapterPosition)
                true
            }
        }
    }
}
