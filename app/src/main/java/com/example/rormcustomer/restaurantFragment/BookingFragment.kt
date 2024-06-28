package com.example.rormcustomer.restaurantFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rormcustomer.ReservationInfoActivity
import com.example.rormcustomer.RestaurantInfoActivity
import com.example.rormcustomer.adapter.BookingAdapter
import com.example.rormcustomer.databinding.FragmentBookingBinding
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.FirebaseDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding
    private lateinit var bookingAdapter: BookingAdapter
    private var allTimeSlots: ArrayList<String> = ArrayList()
    private lateinit var restaurantId: String
    private var selectedTimeSlot: String? = null
    private var selectedDate: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = (activity as RestaurantInfoActivity).getRestaurantId()
        Log.d("BookingFragment", "Retrieved RestaurantId: $restaurantId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingBinding.inflate(inflater, container, false)
        setupSpinner()
        setupNumOfPaxClickListener()
        setupCalendar()
        fetchRestaurantDetails()
        return binding.root
    }

    private fun setupSpinner() {
        val paxOptions = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paxOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.numOfPaxSpinner.adapter = adapter
    }

    private fun setupNumOfPaxClickListener() {
        binding.numOfPax.setOnClickListener {
            binding.numOfPaxSpinner.performClick()
        }
    }

    private fun setupCalendar() {
        binding.bookingCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            generateTimeSlotsForSelectedDate()
        }
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(allTimeSlots)

        // Use GridLayoutManager to display 5 items per row
        val layoutManager = GridLayoutManager(requireContext(), 5)
        binding.bookingTimeRecyclerView.layoutManager = layoutManager
        binding.bookingTimeRecyclerView.adapter = bookingAdapter

        bookingAdapter.setOnItemClickListener { timeSlot ->
            selectedTimeSlot = timeSlot
        }
    }

    private fun fetchRestaurantDetails() {
        val restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId)
        restaurantRef.get().addOnSuccessListener { dataSnapshot ->
            val restaurant = dataSnapshot.getValue(Restaurant::class.java)
            if (restaurant != null) {
                generateTimeSlots(restaurant.startTime, restaurant.endTime)
                setupRecyclerView()

                // Store the restaurant name for later use
                binding.bookButton.setOnClickListener {
                    val numOfPax = binding.numOfPaxSpinner.selectedItem.toString().toInt()
                    val bookingDateTime = getSelectedDateTime()

                    if (bookingDateTime != null) {
                        showConfirmationDialog(numOfPax, bookingDateTime, restaurant.name)
                    } else {
                        Toast.makeText(requireContext(), "Please select a date and time.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("BookingFragment", "Error fetching restaurant details: ${it.message}")
        }
    }

    private fun generateTimeSlots(startTime: String?, endTime: String?) {
        if (startTime == null || endTime == null) return

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        try {
            val start = timeFormat.parse(startTime)
            val end = timeFormat.parse(endTime)

            if (start != null && end != null) {
                allTimeSlots.clear()
                val calendar = Calendar.getInstance().apply { time = start }

                // Adjust to the next nearest 30-minute interval
                val minute = calendar.get(Calendar.MINUTE)
                if (minute % 30 != 0) {
                    calendar.add(Calendar.MINUTE, 30 - (minute % 30))
                }

                // Generate time slots until reaching or exceeding endTime
                while (calendar.time.before(end)) {
                    allTimeSlots.add(timeFormat.format(calendar.time))
                    calendar.add(Calendar.MINUTE, 30)
                }

                generateTimeSlotsForSelectedDate()
            } else {
                Log.e("BookingFragment", "Failed to parse start or end time.")
            }
        } catch (e: ParseException) {
            Log.e("BookingFragment", "Error parsing start or end time: ${e.message}")
        }
    }

    private fun generateTimeSlotsForSelectedDate() {
        val currentTime = Calendar.getInstance()
        val todayDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (selectedDate == null) return

        val selectedDateWithoutTime = Calendar.getInstance().apply {
            timeInMillis = selectedDate!!.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (selectedDateWithoutTime.before(todayDate)) {
            Toast.makeText(requireContext(), "Selected date cannot be earlier than today.", Toast.LENGTH_SHORT).show()
            return
        }

        bookingAdapter.updateTimeSlots(ArrayList(allTimeSlots.filter {
            if (selectedDateWithoutTime == todayDate) {
                val selectedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(it)
                selectedTime?.after(currentTime.time) ?: false
            } else {
                true
            }
        }))
    }

    private fun getSelectedDateTime(): String? {
        selectedDate?.let { date ->
            selectedTimeSlot?.let { time ->
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                return "${dateFormat.format(date.time)} $time"
            }
        }
        return null
    }

    private fun showConfirmationDialog(numOfPax: Int, bookingDateTime: String, restaurantName: String?) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirm Reservation")
            setMessage("Pax: $numOfPax\nTime: $selectedTimeSlot\nDate: ${SimpleDateFormat("yyyy-MM-dd").format(selectedDate?.time)}")
            setPositiveButton("Confirm") { _, _ ->
                val intent = Intent(requireContext(), ReservationInfoActivity::class.java).apply {
                    putExtra("numOfPax", numOfPax)
                    putExtra("bookingDateTime", bookingDateTime) // Change key from bookingDate to bookingDateTime
                    putExtra("bookingTime", selectedTimeSlot)
                    putExtra("restaurantName", restaurantName)
                    putExtra("restaurantId", restaurantId)
                }
                startActivity(intent)
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }
}
