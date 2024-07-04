package com.example.rormcustomer.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.adapter.SavedRestaurantAdapter
import com.example.rormcustomer.databinding.FragmentSavedBinding
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.*

class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var savedRestaurantsList: MutableList<Restaurant>
    private lateinit var adapter: SavedRestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("savedRestaurants")
        savedRestaurantsList = mutableListOf()

        // Set up RecyclerView and LayoutManager
        binding.savedRecyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = SavedRestaurantAdapter(savedRestaurantsList)
        binding.savedRecyclerView.adapter = adapter

        fetchSavedRestaurants()

        return binding.root
    }

    private fun fetchSavedRestaurants() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedRestaurantsList.clear()
                for (restaurantSnapshot in snapshot.children) {
                    val restaurant = restaurantSnapshot.getValue(Restaurant::class.java)
                    if (restaurant != null) {
                        savedRestaurantsList.add(restaurant)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SaveFragment", "Failed to fetch saved restaurants", error.toException())
            }
        })
    }
}
