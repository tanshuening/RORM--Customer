package com.example.rormcustomer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rormcustomer.PastReservationInfoActivity
import com.example.rormcustomer.adapter.PastReservationAdapter
import com.example.rormcustomer.adapter.UpcomingReservationAdapter
import com.example.rormcustomer.databinding.FragmentHistoryBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var pastReservationAdapter: PastReservationAdapter
    private lateinit var upcomingReservationAdapter: UpcomingReservationAdapter
    private val pastReservations = mutableListOf<Reservation>()
    private val upcomingReservations = mutableListOf<Reservation>()
    private val restaurants = mutableMapOf<String, Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("reservations")

        pastReservationAdapter = PastReservationAdapter(pastReservations).apply {
            setOnItemClickListener { reservation ->
                val restaurant = restaurants[reservation.restaurantId]
                if (restaurant != null) {
                    onReservationClick(reservation, restaurant)
                }
            }
        }

        upcomingReservationAdapter = UpcomingReservationAdapter(upcomingReservations)

        binding.pastRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.pastRecyclerView.adapter = pastReservationAdapter

        binding.upcomingRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.upcomingRecyclerView.adapter = upcomingReservationAdapter

        fetchReservations()
    }

    private fun onReservationClick(reservation: Reservation, restaurant: Restaurant) {
        val intent = Intent(requireContext(), PastReservationInfoActivity::class.java).apply {
            putExtra("reservationId", reservation.reservationId)
            putExtra("restaurantId", reservation.restaurantId)
            putExtra("userId", reservation.userId)
            putExtra("restaurantName", restaurant.name)
            putExtra("restaurantImage", restaurant.images?.firstOrNull() ?: "")
            putExtra("numOfPax", reservation.numOfPax)
            putExtra("date", reservation.date)
            putExtra("timeSlot", reservation.timeSlot)
            putExtra("specialRequest", reservation.specialRequest)
            putExtra("occasion", reservation.bookingOccasion)
            putExtra("phone", reservation.bookingPhone)
        }
        startActivity(intent)
    }

    private fun fetchReservations() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val query = database.orderByChild("userId").equalTo(userId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pastReservations.clear()
                    upcomingReservations.clear()
                    for (reservationSnapshot in snapshot.children) {
                        val reservation = reservationSnapshot.getValue(Reservation::class.java)
                        if (reservation != null) {
                            val currentTime = System.currentTimeMillis()
                            if (reservation.date < currentTime) {
                                pastReservations.add(reservation)
                            } else {
                                upcomingReservations.add(reservation)
                                loadRestaurantDetails(reservation.restaurantId)
                            }
                        }
                    }
                    pastReservationAdapter.notifyDataSetChanged()
                    upcomingReservationAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }

    private fun loadRestaurantDetails(restaurantId: String) {
        val restaurantsRef = FirebaseDatabase.getInstance().reference.child("restaurants").child(restaurantId)
        restaurantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                restaurant?.let {
                    restaurants[restaurantId] = it
                    upcomingReservationAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
