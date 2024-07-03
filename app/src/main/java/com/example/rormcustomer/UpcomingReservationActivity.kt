package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.databinding.ActivityUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class UpcomingReservationActivity : AppCompatActivity() {
/*    private lateinit var binding: ActivityUpcomingReservationBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reservationsRef: DatabaseReference
    private lateinit var restaurantsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: UpcomingReservationAdapter
    private val reservations = mutableListOf<Reservation>()
    private val restaurants = mutableMapOf<String, Restaurant>()
    private var selectedReservationId: String? = null
    private var currentRestaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the current restaurant ID from the Intent
        currentRestaurantId = intent.getStringExtra("restaurantId")

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid ?: return

        adapter = UpcomingReservationAdapter(
            reservations,
            restaurants,
            { reservation ->
                val intent = Intent(this, ReservationInfoActivity::class.java)
                intent.putExtra("reservationId", reservation.reservationId)
                intent.putExtra("bookingTime", reservation.timeSlot)
                intent.putExtra("bookingDate", reservation.date)
                startActivity(intent)
            },
            { reservation, position ->
                selectedReservationId = reservation.reservationId
            }
        )

        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reservationRecyclerView.adapter = adapter

        reservationsRef = database.getReference("reservations")
        restaurantsRef = database.getReference("restaurants")

        loadReservations(userId)
    }

    private fun loadReservations(userId: String) {
        val currentTime = System.currentTimeMillis()
        val query = reservationsRef.orderByChild("userId").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservations.clear()
                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(Reservation::class.java)
                    reservation?.let {
                        if (it.date > currentTime && it.restaurantId == currentRestaurantId) {
                            reservations.add(it)
                            loadRestaurantDetails(it.restaurantId)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpcomingReservationActivity", "Error loading reservations: ${error.message}")
            }
        })
    }

    private fun loadRestaurantDetails(restaurantId: String) {
        restaurantsRef.child(restaurantId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                restaurant?.let {
                    restaurants[restaurantId] = it
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpcomingReservationActivity", "Error loading restaurant details: ${error.message}")
            }
        })
    }

    fun checkReservationAvailability(reservationId: String, callback: (Boolean) -> Unit) {
        val ordersRef = database.getReference("orders")
        ordersRef.orderByChild("reservationDetails/reservationId").equalTo(reservationId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(!snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UpcomingReservationActivity", "Error checking reservation: ${error.message}")
                    callback(false)
                }
            })
    }*/
}
