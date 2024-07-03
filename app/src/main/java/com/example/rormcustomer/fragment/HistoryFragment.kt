package com.example.rormcustomer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.PastReservationAdapter
import com.example.rormcustomer.databinding.FragmentHistoryBinding
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var pastReservationAdapter: PastReservationAdapter
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

        database = FirebaseDatabase.getInstance().reference.child("reservations")

/*        pastReservationAdapter = PastReservationAdapter(pastReservations)
        upcomingReservationAdapter = UpcomingReservationAdapter(
            upcomingReservations,
            restaurants,
            { reservation ->
                // Handle item click
                // Example: Show a detailed view of the reservation
            },
            { reservation, position ->
                // Handle item selection (long click)
                // Example: Show options to edit or delete the reservation
            }
        )*/

        binding.pastRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.pastRecyclerView.adapter = pastReservationAdapter

        binding.upcomingRecyclerView.layoutManager = LinearLayoutManager(context)

        fetchReservations()
    }

    private fun fetchReservations() {
        database.addValueEventListener(object : ValueEventListener {
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
               // upcomingReservationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun loadRestaurantDetails(restaurantId: String) {
        val restaurantsRef = FirebaseDatabase.getInstance().reference.child("restaurants").child(restaurantId)
        restaurantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                restaurant?.let {
                    restaurants[restaurantId] = it
                 //   upcomingReservationAdapter.notifyDataSetChanged()
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
