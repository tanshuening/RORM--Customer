package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityReservationInfoBinding
import com.example.rormcustomer.models.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ReservationInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve reservation details from intent
        val restaurantId = intent.getStringExtra("restaurantId") ?: ""
        val restaurantName = intent.getStringExtra("restaurantName") ?: ""
        val numOfPax = intent.getIntExtra("numOfPax", 1)
        val bookingDateTime = intent.getStringExtra("bookingDateTime") ?: ""

        // Format the booking date and time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val date = dateFormat.parse(bookingDateTime)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormatOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormatOnly = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Set booking details in the UI
        binding.bookingName.text = restaurantName
        binding.bookingNumberOfPax.text = "$numOfPax"
        binding.bookingDate.text = dateFormatOnly.format(date)
        binding.bookingTime.text = timeFormatOnly.format(date)
        binding.bookingDays.text = dayFormat.format(date)

        binding.bookButton.setOnClickListener {
            val phoneNumber = binding.bookingPhone.text.toString().trim()
            if (phoneNumber.length !in 9..10) {
                binding.bookingPhone.error = "Phone number must be between 9 and 10 digits."
                return@setOnClickListener
            }

            val specialRequest = binding.bookingSpecialRequest.text.toString()
            val bookingOccasion = getSelectedOccasion()
            if (bookingOccasion == null) {
                Toast.makeText(this, "Please select an occasion.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reservationId = UUID.randomUUID().toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            val reservation = Reservation(
                reservationId = reservationId,
                restaurantId = restaurantId,
                userId = userId,
                date = date.time,
                timeSlot = timeFormatOnly.format(date),
                numOfPax = numOfPax,
                specialRequest = specialRequest,
                bookingOccasion = bookingOccasion,
                bookingPhone = phoneNumber
            )

            val databaseReference = FirebaseDatabase.getInstance().getReference("reservations")
            databaseReference.child(reservationId).setValue(reservation)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reservation booked successfully!", Toast.LENGTH_SHORT).show()
                        showOrderDialog(restaurantId)
                    } else {
                        Toast.makeText(this, "Failed to book reservation: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun showOrderDialog(restaurantId: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Place Order")
            .setMessage("Would you like to place an order now?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, RestaurantInfoActivity::class.java).apply {
                    putExtra("RestaurantId", restaurantId)
                    putExtra("RestaurantName", binding.bookingName.text.toString())
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .create()
        dialog.show()
    }

    fun onOccasionButtonClicked(view: View) {
        binding.birthday.isSelected = false
        binding.anniversary.isSelected = false
        binding.date.isSelected = false
        binding.businessMeal.isSelected = false
        binding.specialOccasion.isSelected = false

        view.isSelected = true
    }

    private fun getSelectedOccasion(): String? {
        return when {
            binding.birthday.isSelected -> "Birthday"
            binding.anniversary.isSelected -> "Anniversary"
            binding.date.isSelected -> "Date"
            binding.businessMeal.isSelected -> "Business Meal"
            binding.specialOccasion.isSelected -> "Special Occasion"
            else -> null
        }
    }
}
