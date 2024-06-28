package com.example.rormcustomer.restaurantFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.R
import com.example.rormcustomer.adapter.ReviewAdapter
import com.example.rormcustomer.databinding.FragmentReviewsBinding

class ReviewsFragment : Fragment() {
    private lateinit var binding: FragmentReviewsBinding
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val customerName = arrayListOf("Customer Name 1", "Customer Name 2", "Customer Name 3")
        val date = arrayListOf("Date 1", "Date 2", "Date 3")
        val review = arrayListOf("Review 1", "Review 2", "Review 3")
        val profilePicture = arrayListOf(R.drawable.profile_pic, R.drawable.profile_pic, R.drawable.profile_pic)
        reviewAdapter = ReviewAdapter(customerName, profilePicture, date, review)

        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.reviewRecyclerView.adapter = reviewAdapter
    }

    companion object {

    }

}
