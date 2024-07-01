package com.example.rormcustomer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rormcustomer.R
import com.example.rormcustomer.adapter.PastReservationAdapter
import com.example.rormcustomer.adapter.UpcomingReservationAdapter
import com.example.rormcustomer.models.Reservation
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var pastRecyclerView: RecyclerView
    private lateinit var pastAdapter: PastReservationAdapter
    private lateinit var upcomingAdapter: UpcomingReservationAdapter
    private val pastReservations = mutableListOf<Reservation>()
    private val upcomingReservations = mutableListOf<Reservation>()
    private val restaurantsMap = mutableMapOf<String, Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        //upcomingRecyclerView = view.findViewById(R.id.upcomingRecyclerView)
        pastRecyclerView = view.findViewById(R.id.pastRecyclerView)

        pastAdapter = PastReservationAdapter(pastReservations)
        upcomingAdapter = UpcomingReservationAdapter(
            upcomingReservations,
            restaurantsMap,
            onItemClick = { reservation ->
                // Handle item click
            },
            onItemLongClick = { reservation, position ->
                // Handle item long click
            }
        )

        pastRecyclerView.layoutManager = GridLayoutManager(context, 2)
        upcomingRecyclerView.layoutManager = GridLayoutManager(context, 2)

        pastRecyclerView.adapter = pastAdapter
        upcomingRecyclerView.adapter = upcomingAdapter

        fetchReservations()

        return view
    }

    private fun fetchReservations() {
        val database = FirebaseDatabase.getInstance()
        val reservationsRef = database.getReference("orders")

        reservationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pastReservations.clear()
                upcomingReservations.clear()

                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(Reservation::class.java)
                    reservation?.let {
                        fetchRestaurantDetails(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchRestaurantDetails(reservation: Reservation) {
        val database = FirebaseDatabase.getInstance()
        val restaurantRef = database.getReference("restaurants").child(reservation.restaurantId)

        restaurantRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                restaurant?.let {
                    restaurantsMap[it.restaurantId ?: ""] = it
                    reservation.restaurant = it
                    if (isUpcoming(reservation.date)) {
                        upcomingReservations.add(reservation)
                        upcomingAdapter.notifyDataSetChanged()
                    } else {
                        pastReservations.add(reservation)
                        pastAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun isUpcoming(bookingDate: Long): Boolean {
        val reservationDate = Date(bookingDate)
        val currentDate = Calendar.getInstance().time
        return reservationDate.after(currentDate)
    }
}
