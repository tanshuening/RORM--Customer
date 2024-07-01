package com.example.rormcustomer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.R
import com.example.rormcustomer.databinding.CardViewPastReservationsBinding
import com.example.rormcustomer.models.Reservation

class PastReservationAdapter(private val pastReservations: List<Reservation>) :
    RecyclerView.Adapter<PastReservationAdapter.PastReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_past_reservations, parent, false)
        return PastReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastReservationViewHolder, position: Int) {
        val reservation = pastReservations[position]
        holder.bind(reservation)
    }

    override fun getItemCount(): Int = pastReservations.size

    class PastReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val restaurantImage: ImageView = itemView.findViewById(R.id.restaurantImage)
        private val restaurantName: TextView = itemView.findViewById(R.id.restaurantName)
        //private val restaurantRatings: TextView = itemView.findViewById(R.id.restaurantRatings)
        private val restaurantTag: TextView = itemView.findViewById(R.id.restaurantTag)

        fun bind(reservation: Reservation) {
            reservation.restaurant?.let {
                restaurantName.text = it.name
/*
                restaurantRatings.text = it.ratings  // Assuming ratings are stored in the price field
*/
                restaurantTag.text = it.cuisine
                // Load image using an image loading library like Glide or Picasso
                it.images?.firstOrNull()?.let { imageUrl ->
                    Glide.with(itemView.context).load(imageUrl).into(restaurantImage)
                }
            }
        }
    }
}
