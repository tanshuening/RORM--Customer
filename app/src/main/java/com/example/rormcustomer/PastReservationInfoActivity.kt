package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityPastReservationInfoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PastReservationInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPastReservationInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPastReservationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reservationId = intent.getStringExtra("reservationId")
        val restaurantId = intent.getStringExtra("restaurantId")
        val userId = intent.getStringExtra("userId")
        val restaurantName = intent.getStringExtra("restaurantName")
        val restaurantImage = intent.getStringExtra("restaurantImage")
        val numOfPax = intent.getIntExtra("numOfPax", 0)
        val date = intent.getLongExtra("date", 0L)
        val timeSlot = intent.getStringExtra("timeSlot")
        val specialRequest = intent.getStringExtra("specialRequest")
        val occasion = intent.getStringExtra("occasion")
        val phone = intent.getStringExtra("phone")

        // Ensure data is received correctly
        if (reservationId == null || restaurantId == null || userId == null || restaurantName == null) {
            // Handle missing data
            finish()
            return
        }

        binding.restaurantName.text = restaurantName
        // Glide.with(this).load(restaurantImage).into(binding.restaurantImage)
        binding.bookingNumberOfPax.text = numOfPax.toString()
        //binding.bookingDate.text = formatDate(date)
        binding.bookingTime.text = timeSlot
        binding.bookingSpecialRequest.setText(specialRequest)
        binding.occasion.setText(occasion)
        binding.bookingPhone.setText(phone)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.feedbackButton.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java).apply {
                putExtra("reservationId", reservationId)
                putExtra("restaurantId", restaurantId)
                putExtra("userId", userId)
                putExtra("restaurantName", restaurantName)
            }
            startActivity(intent)
        }
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("ms", "MY"))
        return sdf.format(Date(date))
    }
}
