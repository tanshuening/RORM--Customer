package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.UpcomingReservationAdapter
import com.example.rormcustomer.databinding.ActivityUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UpcomingReservationActivity : AppCompatActivity() {
/*
    private lateinit var binding: ActivityUpcomingReservationBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reservationsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: UpcomingReservationAdapter
    private val reservations = mutableListOf<Reservation>()
    private var selectedReservationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid ?: return

        adapter = UpcomingReservationAdapter(
            reservations,
            null,
            { reservation ->
                // Handle item click
                val intent = Intent(this, ReservationInfoActivity::class.java)
                intent.putExtra("reservationId", reservation.reservationId)
                intent.putExtra("bookingTime", reservation.timeSlot)
                intent.putExtra("bookingDate", reservation.date)
                startActivity(intent)
            },
            { reservation, position ->
                selectedReservationId = reservation.reservationId
                // Handle item long click (if needed)
            }
        )

        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reservationRecyclerView.adapter = adapter

        reservationsRef = database.getReference("reservations")

        loadReservations(userId)

        binding.addButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Choose reservation ...")
                .setMessage("Do you want to add this reservation?")
                .setPositiveButton("Yes") { dialog, which ->
                    saveReservationToOrderDatabase(selectedReservationId)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun loadReservations(userId: String) {
        val query = reservationsRef.orderByChild("userId").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservations.clear()
                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(Reservation::class.java)
                    reservation?.let {
                        reservations.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpcomingReservationActivity", "Error loading reservations: ${error.message}")
            }
        })
    }

    private fun saveReservationToOrderDatabase(reservationId: String?) {
        if (reservationId != null) {
            val orderDatabase = FirebaseDatabase.getInstance().getReference("orders")
            val newOrderId = orderDatabase.push().key
            if (newOrderId != null) {
                val orderData = hashMapOf(
                    "reservationId" to reservationId,
                    // Add other necessary fields here
                )
                orderDatabase.child(newOrderId).setValue(orderData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Handle success
                        } else {
                            // Handle failure
                        }
                    }
            }
        }
    }*/
}
