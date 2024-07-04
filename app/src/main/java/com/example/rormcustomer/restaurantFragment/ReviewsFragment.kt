package com.example.rormcustomer.restaurantFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.RestaurantInfoActivity
import com.example.rormcustomer.adapter.ReviewAdapter
import com.example.rormcustomer.databinding.FragmentReviewsBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewsFragment : Fragment() {

    private lateinit var binding: FragmentReviewsBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var restaurantId: String

    private val customerNames = ArrayList<String>()
    private val profilePictures = ArrayList<String>()
    private val dates = ArrayList<String>()
    private val reviews = ArrayList<String>()
    private val ratings = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantId = (activity as RestaurantInfoActivity).getRestaurantId()
        setupRecyclerView()
        fetchFeedbacks()
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(customerNames, profilePictures, dates, reviews, ratings)
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reviewRecyclerView.adapter = reviewAdapter
    }

    private fun fetchFeedbacks() {
        databaseReference = FirebaseDatabase.getInstance().getReference("feedbacks")

        databaseReference.orderByChild("restaurantId").equalTo(restaurantId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (feedbackSnapshot in dataSnapshot.children) {
                            val userId = feedbackSnapshot.child("userId").value.toString()
                            val review = feedbackSnapshot.child("review").value.toString()
                            val rating = feedbackSnapshot.child("rating").getValue(Int::class.java) ?: 0
                            val timestamp = feedbackSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L

                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val date = sdf.format(Date(timestamp))

                            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val customerName = userSnapshot.child("name").value.toString()
                                    val profileImageUrl = userSnapshot.child("profileImageUrl").value.toString()

                                    customerNames.add(customerName)
                                    profilePictures.add(profileImageUrl)
                                    dates.add(date)
                                    reviews.add(review)
                                    ratings.add(rating)

                                    reviewAdapter.updateData(customerNames, profilePictures, dates, reviews, ratings)
                                }

                                override fun onCancelled(userError: DatabaseError) {
                                    Log.e("ReviewsFragment", "Failed to load user data", userError.toException())
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("ReviewsFragment", "Failed to load feedbacks", databaseError.toException())
                }
            })
    }
}
