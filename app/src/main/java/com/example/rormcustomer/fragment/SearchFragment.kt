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

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RestaurantAdapter

    private val originalRestaurantName = listOf("iVegan", "Sando", "Colony Bakery")
    private val originalRestaurantRatings = listOf("5.0", "4.5", "4.0")
    private val originalRestaurantTag = listOf("Vegan", "Bakery", "Bakery")
    private val originalRestaurantImage =
        listOf(R.drawable.restaurant, R.drawable.restaurant2, R.drawable.restaurant3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val filteredRestaurantName = mutableListOf<String>()
    private val filteredRestaurantRatings = mutableListOf<String>()
    private val filteredRestaurantTag = mutableListOf<String>()
    private val filteredRestaurantImage = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

/*                adapter = RestaurantAdapter(
                    filteredRestaurantName, filteredRestaurantRatings, filteredRestaurantTag, filteredRestaurantImage, requireContext()
                )*/
        binding.restaurantRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.restaurantRecycleView.adapter = adapter

        // search view
        setupSearchView()

        showAllMenu()

        return binding.root
    }

    private fun showAllMenu() {
        filteredRestaurantName.clear()
        filteredRestaurantRatings.clear()
        filteredRestaurantTag.clear()
        filteredRestaurantImage.clear()

        filteredRestaurantName.addAll(originalRestaurantName)
        filteredRestaurantRatings.addAll(originalRestaurantRatings)
        filteredRestaurantTag.addAll(originalRestaurantTag)
        filteredRestaurantImage.addAll(originalRestaurantImage)

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
        filteredRestaurantName.clear()
        filteredRestaurantRatings.clear()
        filteredRestaurantTag.clear()
        filteredRestaurantImage.clear()

        originalRestaurantName.forEachIndexed { index, restaurantName ->
            if (restaurantName.contains(query.toString(), ignoreCase = true)) {
                filteredRestaurantName.add(restaurantName)
                filteredRestaurantRatings.add(originalRestaurantRatings[index])
                filteredRestaurantTag.add(originalRestaurantTag[index])
                filteredRestaurantImage.add(originalRestaurantImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {

    }
}