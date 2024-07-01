package com.example.rormcustomer.adapter

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.UpcomingReservationActivity
import com.example.rormcustomer.databinding.CardViewUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import java.text.SimpleDateFormat
import java.util.*

class UpcomingReservationAdapter(
    private val reservations: List<Reservation>,
    private val restaurants: Map<String, Restaurant>,
    private val onItemClick: (Reservation) -> Unit,
    private val onItemLongClick: (Reservation, Int) -> Unit
) : RecyclerView.Adapter<UpcomingReservationAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = CardViewUpcomingReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        val restaurant = restaurants[reservation.restaurantId]
        holder.bind(reservation, restaurant)
    }

    override fun getItemCount(): Int = reservations.size

    inner class ReservationViewHolder(private val binding: CardViewUpcomingReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation, restaurant: Restaurant?) {
            binding.bookingNumOfPax.text = reservation.numOfPax.toString()
            binding.bookingDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(reservation.date))
            binding.bookingTime.text = reservation.timeSlot
            binding.restaurantName.text = restaurant?.name ?: "Unknown Restaurant"
            restaurant?.images?.firstOrNull()?.let { imageUrl ->
                Glide.with(binding.restaurantImage.context)
                    .load(imageUrl)
                    .into(binding.restaurantImage)
            }

            binding.root.setOnClickListener {
                onItemClick(reservation)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(reservation, adapterPosition)
                true
            }

            binding.addButton.setOnClickListener {
                val activity = binding.root.context as? UpcomingReservationActivity
                activity?.checkReservationAvailability(reservation.reservationId) { isAvailable ->
                    if (isAvailable) {
                        AlertDialog.Builder(binding.root.context)
                            .setTitle("Reservation")
                            .setMessage("Do you want to use this reservation?")
                            .setPositiveButton("Yes") { dialog, which ->
                                val resultIntent = Intent().apply {
                                    putExtra("selectedReservationId", reservation.reservationId)
                                    putExtra("selectedNumOfPax", reservation.numOfPax)
                                    putExtra("selectedDate", reservation.date)
                                    putExtra("selectedTimeSlot", reservation.timeSlot)
                                }
                                activity.setResult(RESULT_OK, resultIntent)
                                activity.finish()
                            }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        AlertDialog.Builder(binding.root.context)
                            .setTitle("Reservation Unavailable")
                            .setMessage("This reservation is already linked to an order.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }
        }
    }
}
