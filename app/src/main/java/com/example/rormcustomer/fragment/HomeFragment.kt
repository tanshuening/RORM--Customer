package com.example.rormcustomer.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.rormcustomer.CartActivity
import com.example.rormcustomer.R
import com.example.rormcustomer.adapter.BookingTimeAdapter
import com.example.rormcustomer.adapter.RestaurantAdapter
import com.example.rormcustomer.databinding.FragmentHomeBinding
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var restaurantList: ArrayList<Restaurant>
    private var selectedPax: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        restaurantList = ArrayList()

        setupRecyclerViews()
        setupImageSlider()
        setupFloatingActionButton()
        setupToolbar()
        setupBookingInfoDialog()
        fetchRestaurants()
    }

    private fun fetchRestaurants(selectedLocation: String? = null) {
        val restaurantRef = database.reference.child("restaurants")

        restaurantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                restaurantList.clear()
                for (dataSnapshot in snapshot.children) {
                    val restaurant = dataSnapshot.getValue(Restaurant::class.java)
                    restaurant?.let {
                        restaurantList.add(it)
                        Log.d("HomeFragment", "Restaurant fetched: ${it.name}")
                    }
                }

                // Filter restaurants based on selected location if provided
                if (!selectedLocation.isNullOrEmpty() && selectedLocation != "All Locations") {
                    val filteredList = restaurantList.filter {
                        it.location.equals(selectedLocation, ignoreCase = true)
                    }
                    restaurantList.clear()
                    restaurantList.addAll(filteredList)
                }

                // Notify adapter about data changes
                binding.recommendedRestaurantRecyclerView.adapter?.notifyDataSetChanged()

                // Check if no restaurants were fetched
                if (restaurantList.isEmpty()) {
                    Toast.makeText(requireContext(), "No restaurants found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to fetch restaurants", error.toException())
                Toast.makeText(requireContext(), "Failed to load restaurants", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupToolbar() {
        val locationSpinner = binding.root.findViewById<Spinner>(R.id.locationSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.malaysia_states,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter

            // Set up listener for spinner item selection
            locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedLocation = parent?.getItemAtPosition(position).toString()
                    fetchRestaurants(if (selectedLocation == "All Locations") null else selectedLocation)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected
                }
            }
        }

        setCurrentTime()
    }

    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>().apply {
            add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
            add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        }

        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }

    private fun setupRecyclerViews() {
        binding.recommendedRestaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = RestaurantAdapter(restaurantList, true, requireContext())
        binding.recommendedRestaurantRecyclerView.adapter = adapter
    }

    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBookingInfoDialog() {
        val bookingInfo = binding.root.findViewById<LinearLayout>(R.id.bookingInfo)
        bookingInfo.setOnClickListener {
            dialog = Dialog(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.dialog_booking, null)

            dialogView.findViewById<TextView>(R.id.closeButton).setOnClickListener {
                closeDialog()
            }

            val paxSpinner = dialogView.findViewById<Spinner>(R.id.paxSpinner)
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.party_sizes,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                paxSpinner.adapter = adapter
            }

            paxSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedPax = parent.getItemAtPosition(position).toString()
                    checkAndCloseDialog()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            val datePicker = dialogView.findViewById<CalendarView>(R.id.datePicker)
            datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = format.format(calendar.time)
                checkAndCloseDialog()
            }

            val timeSlot = listOf(
                "07:00AM", "07:30AM", "08:00AM", "08:30AM", "09:00AM", "09:30AM",
                "10:00AM", "10:30AM", "11:00AM", "11:30AM", "12:00PM", "12:30PM",
                "01:00PM", "01:30PM", "02:00PM", "02:30PM", "03:00PM", "03:30PM",
                "04:00PM", "04:30PM", "05:00PM", "05:30PM", "06:00PM", "06:30PM",
                "07:00PM", "07:30PM", "08:00PM", "08:30PM", "09:00PM", "09:30PM",
                "10:00PM", "10:30PM", "11:00PM", "11:30PM", "12:00AM"
            )

            val bookingTimeAdapter = BookingTimeAdapter(timeSlot) { time ->
                selectedTime = time
                checkAndCloseDialog()
            }

            val timeRecyclerView = dialogView.findViewById<RecyclerView>(R.id.timeRecyclerView)
            timeRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = bookingTimeAdapter
            }

            dialog.setContentView(dialogView)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }
    }

    private fun checkAndCloseDialog() {
        if (selectedPax != null && selectedDate != null && selectedTime != null) {
            val partySizeTextView = binding.root.findViewById<TextView>(R.id.partySize)
            val dateTextView = binding.root.findViewById<TextView>(R.id.date)
            val timeTextView = binding.root.findViewById<TextView>(R.id.currentTime)

            partySizeTextView.text = selectedPax
            dateTextView.text = if (isToday(selectedDate!!)) "Today" else selectedDate
            timeTextView.text = selectedTime

            dialog.dismiss()
            // Reset selections for next use
            selectedPax = null
            selectedDate = null
            selectedTime = null
        }
    }

    private fun isToday(date: String): Boolean {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        return date == today
    }

    /*    private fun setupToolbar() {
            val locationSpinner = binding.root.findViewById<Spinner>(R.id.locationSpinner)
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.malaysia_states,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                locationSpinner.adapter = adapter
            }

            setCurrentTime()
        }*/

    private fun setCurrentTime() {
        val timeTextView = binding.root.findViewById<TextView>(R.id.currentTime)
        timeTextView?.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    private fun closeDialog() {
        dialog.dismiss()
    }
}