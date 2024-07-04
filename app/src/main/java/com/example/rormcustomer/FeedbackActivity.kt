package com.example.rormcustomer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityFeedbackBinding
import com.example.rormcustomer.models.Feedback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        val reservationId = intent.getStringExtra("reservationId")
        val restaurantId = intent.getStringExtra("restaurantId")
        val userId = intent.getStringExtra("userId")
        val restaurantName = intent.getStringExtra("restaurantName")

        Log.d("FeedbackActivity", "reservationId: $reservationId, restaurantId: $restaurantId, userId: $userId, restaurantName: $restaurantName")

        if (reservationId == null || restaurantId == null || userId == null) {
            Toast.makeText(this, "Missing required data.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.restaurantName.text = restaurantName

        binding.submitButton.setOnClickListener {
            submitFeedback(reservationId, restaurantId, userId)
        }
    }

    private fun submitFeedback(reservationId: String, restaurantId: String, userId: String) {
        val rating = binding.spinnerRating.selectedItem.toString().toIntOrNull()
        val review = binding.editTextReview.text.toString()

        if (rating == null || review.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val feedbackId = database.child("feedbacks").push().key
        if (feedbackId == null) {
            Toast.makeText(this, "Failed to generate feedback ID", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = Date().time

        val feedback = Feedback(
            feedbackId,
            userId,
            restaurantId,
            reservationId,
            rating,
            review,
            timestamp
        )

        database.child("feedbacks").child(feedbackId).setValue(feedback).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
