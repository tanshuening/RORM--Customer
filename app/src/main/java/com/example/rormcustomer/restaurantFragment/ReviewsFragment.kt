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
        val customerName = arrayListOf("Tan Shue Ning")
        val date = arrayListOf("01/07/2024")
        val review = arrayListOf("Review 1....")
        //val profilePicture = arrayListOf(R.drawable.profile_pic, R.drawable.profile_pic, R.drawable.profile_pic)
        reviewAdapter = ReviewAdapter(customerName, date, review)

        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.reviewRecyclerView.adapter = reviewAdapter
    }

    companion object {

    }

}
