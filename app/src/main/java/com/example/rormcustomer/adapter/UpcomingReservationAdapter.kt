/*
package com.examples.rormcustomer.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.databinding.CardViewUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.User
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

cl*/
/*ass UpcomingReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<UpcomingReservationAdapter.ViewHolder>() {

*//*
*/
/*    inner class ViewHolder(val binding: CardViewUpcomingReservationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardViewUpcomingReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]

        holder.binding.bookingNumOfPax.text = reservation.numOfPax.toString()
        holder.binding.bookingDate.text = formatDate(reservation.date)
        holder.binding.bookingTime.text = reservation.timeSlot

        // Fetch user data from Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(reservation.userId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                holder.binding.restaurantName.text = user?.name ?: "Unknown"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("UpcomingReservationAdapter", "Failed to load user data", databaseError.toException())
                holder.binding.restaurantName.text = "Unknown"
            }
        })

        // Set click listener
        holder.binding.root.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, UpcomingReservationInfoActivity::class.java).apply {
                putExtra("reservationId", reservation.reservationId)
                putExtra("userId", reservation.userId)
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

    override fun getItemCount() = reservations.size

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("ms", "MY"))
        return sdf.format(Date(date))
    }*//*

}
*/
