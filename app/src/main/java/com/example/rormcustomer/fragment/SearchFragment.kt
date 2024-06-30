package com.example.rormcustomer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rormcustomer.R
import com.example.rormcustomer.adapter.RestaurantAdapter
import com.example.rormcustomer.databinding.FragmentSearchBinding
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.*

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RestaurantAdapter
    private lateinit var database: DatabaseReference

    private val originalRestaurantList = mutableListOf<Restaurant>()
    private val filteredRestaurantList = mutableListOf<Restaurant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference.child("restaurants")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = RestaurantAdapter(filteredRestaurantList, false, requireContext()) // Assuming it's not HomeFragment
        binding.restaurantRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.restaurantRecycleView.adapter = adapter

        // Fetch restaurant data from Firebase
        fetchRestaurantData()

        // Search view
        setupSearchView()

        return binding.root
    }

    private fun fetchRestaurantData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalRestaurantList.clear()
                for (restaurantSnapshot in snapshot.children) {
                    val restaurant = restaurantSnapshot.getValue(Restaurant::class.java)
                    restaurant?.let { originalRestaurantList.add(it) }
                }
                showAllRestaurants()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun showAllRestaurants() {
        filteredRestaurantList.clear()
        filteredRestaurantList.addAll(originalRestaurantList)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterRestaurantItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRestaurantItems(newText?.toString())
                return true
            }
        })
    }

    private fun filterRestaurantItems(query: String?) {
        filteredRestaurantList.clear()
        if (query.isNullOrEmpty()) {
            filteredRestaurantList.addAll(originalRestaurantList)
        } else {
            for (restaurant in originalRestaurantList) {
                if (restaurant.name?.contains(query, ignoreCase = true) == true) {
                    filteredRestaurantList.add(restaurant)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {
        // Add any necessary companion object methods or constants here
    }
}
