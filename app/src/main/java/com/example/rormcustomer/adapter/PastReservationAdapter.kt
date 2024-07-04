package com.example.rormcustomer.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rormcustomer.PastReservationInfoActivity
import com.example.rormcustomer.databinding.CardViewReservationsBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.*

class PastReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<PastReservationAdapter.ViewHolder>() {

    private var onItemClickListener: ((Reservation) -> Unit)? = null

    inner class ViewHolder(val binding: CardViewReservationsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardViewReservationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]

        // Fetch restaurant data from Firebase
        val restaurantReference = FirebaseDatabase.getInstance().getReference("restaurants").child(reservation.restaurantId)
        restaurantReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val restaurant = dataSnapshot.getValue(Restaurant::class.java)
                holder.binding.restaurantName.text = restaurant?.name ?: "Unknown"
                Glide.with(holder.itemView.context)
                    .load(restaurant?.images?.get(0)) // Assuming the first image is used
                    .into(holder.binding.restaurantImage)

                // Set click listener to switch to PastReservationInfoActivity
                holder.binding.root.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, PastReservationInfoActivity::class.java).apply {
                        putExtra("reservationId", reservation.reservationId)
                        putExtra("restaurantId", reservation.restaurantId)
                        putExtra("userId", reservation.userId)
                        putExtra("restaurantName", restaurant?.name)
                        putExtra("restaurantImage", restaurant?.images?.get(0))
                        putExtra("numOfPax", reservation.numOfPax)
                        putExtra("date", reservation.date)
                        putExtra("timeSlot", reservation.timeSlot)
                        putExtra("specialRequest", reservation.specialRequest)
                        putExtra("occasion", reservation.bookingOccasion)
                        putExtra("phone", reservation.bookingPhone)
                    }
                    context.startActivity(intent)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("PastReservationAdapter", "Failed to load restaurant data", databaseError.toException())
                holder.binding.restaurantName.text = "Unknown"
            }
        })
    }

    override fun getItemCount() = reservations.size

    fun setOnItemClickListener(listener: (Reservation) -> Unit) {
        this.onItemClickListener = listener
    }
}
