package com.example.rormcustomer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rormcustomer.databinding.ActivityUpcomingReservationInfoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingReservationInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpcomingReservationInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingReservationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reservationId = intent.getStringExtra("reservationId")
        val restaurantName: String? = intent.getStringExtra("restaurantName")
        val restaurantImage = intent.getStringExtra("restaurantImage")
        val numOfPax = intent.getIntExtra("numOfPax", 0)
        val date = intent.getLongExtra("date", 0L)
        val timeSlot = intent.getStringExtra("timeSlot")
        val specialRequest = intent.getStringExtra("specialRequest")
        val occasion = intent.getStringExtra("occasion")
        val phone = intent.getStringExtra("phone")

        binding.restaurantName.text = restaurantName
       // Glide.with(this).load(restaurantImage).into(binding.restaurantImage)
        binding.bookingNumberOfPax.text = numOfPax.toString()
        binding.bookingDate.text = formatDate(date)
        binding.bookingTime.text = timeSlot
        binding.bookingSpecialRequest.setText(specialRequest)
        binding.occasion.setText(occasion)
        binding.bookingPhone.setText(phone)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun formatDate(date: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("ms", "MY"))
        return sdf.format(Date(date))
    }
}
