package com.example.rormcustomer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rormcustomer.R
import com.example.rormcustomer.adapter.PastReservationAdapter
import com.example.rormcustomer.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var pastReservationAdapter: PastReservationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val restaurantName = arrayListOf("iVegan", "Sando", "Colony Bakery")
        val rating = arrayListOf("5.0", "4.5", "4.0")
        val tag = arrayListOf("Vegan", "Bakery", "Bakery")
        val restaurantImage = arrayListOf(R.drawable.restaurant, R.drawable.restaurant2, R.drawable.restaurant3)

        pastReservationAdapter = PastReservationAdapter(restaurantName, rating, tag, restaurantImage)

        binding.pastRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.pastRecyclerView.adapter = pastReservationAdapter
    }
}
