package com.example.rormcustomer

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.rormcustomer.adapter.UpcomingReservationAdapter
import com.example.rormcustomer.databinding.ActivityUpcomingReservationBinding
import com.example.rormcustomer.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class UpcomingReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpcomingReservationBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var reservationQuery: Query
    private lateinit var restaurantRef: DatabaseReference
    private val reservations = mutableListOf<Reservation>()
    private lateinit var adapter: UpcomingReservationAdapter
    private var restaurantId: String? = null
    private var restaurantName: String? = null
    private var restaurantImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        restaurantId = intent.getStringExtra("restaurantId")
        if (restaurantId == null) {
            Log.e("UpcomingReservation", "Restaurant ID is null")
            finish()
            return
        }

        adapter = UpcomingReservationAdapter(reservations, restaurantName, restaurantImage)
        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reservationRecyclerView.adapter = adapter

        binding.reservationAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        loadReservations()
        loadRestaurantDetails()
    }

    private fun loadReservations() {
        val userId = auth.currentUser?.uid ?: return
        val currentTime = System.currentTimeMillis()
        reservationQuery = database.getReference("reservations")
            .orderByChild("userId")
            .equalTo(userId)

        reservationQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservations.clear()
                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(Reservation::class.java)
                    reservation?.let {
                        if (it.restaurantId == restaurantId && it.date >= currentTime) {
                            reservations.add(it)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpcomingReservation", "Error loading reservations: ${error.message}")
            }
        })
    }

    private fun loadRestaurantDetails() {
        restaurantRef = database.getReference("restaurants").child(restaurantId!!)
        restaurantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                restaurantName = snapshot.child("name").getValue(String::class.java)
                restaurantImage = snapshot.child("image").getValue(String::class.java)

                binding.reservationAppBarLayout.findViewById<TextView>(R.id.restaurantName).text = restaurantName
                if (restaurantImage != null) {
                    Glide.with(this@UpcomingReservationActivity)
                        .load(restaurantImage)
                        .into(binding.reservationAppBarLayout.findViewById(R.id.restaurantImage))
                }

                adapter.updateRestaurantDetails(restaurantName, restaurantImage)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpcomingReservation", "Failed to load restaurant details", error.toException())
            }
        })
    }
}
