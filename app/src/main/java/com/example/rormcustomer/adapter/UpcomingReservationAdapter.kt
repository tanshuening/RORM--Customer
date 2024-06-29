package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.CardViewUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingReservationAdapter(
    private val reservations: List<Reservation>,
    private var restaurantName: String?,
    private var restaurantImage: String?
) : RecyclerView.Adapter<UpcomingReservationAdapter.UpcomingReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingReservationViewHolder {
        val binding = CardViewUpcomingReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingReservationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation, restaurantName, restaurantImage)
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    fun updateRestaurantDetails(name: String?, image: String?) {
        restaurantName = name
        restaurantImage = image
        notifyDataSetChanged()
    }

    class UpcomingReservationViewHolder(private val binding: CardViewUpcomingReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation, restaurantName: String?, restaurantImage: String?) {
            binding.restaurantName.text = restaurantName
            binding.bookingNumOfPax.text = reservation.numOfPax.toString()
            binding.bookingDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                Date(reservation.date)
            )
            binding.bookingTime.text = reservation.timeSlot

            if (restaurantImage != null) {
                Glide.with(binding.root.context)
                    .load(restaurantImage)
                    .into(binding.restaurantImage)
            }
        }
    }
}
