package com.example.rormcustomer.restaurantFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rormcustomer.databinding.FragmentDetailsBinding
import com.example.rormcustomer.models.Restaurant
import com.google.firebase.database.*

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var restaurantId: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurantId = it.getString("RestaurantId") ?: ""
        }
        Log.d("DetailsFragment", "Received RestaurantId: $restaurantId")  // Ensure this log
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (restaurantId.isNotEmpty()) {
            database = FirebaseDatabase.getInstance().getReference("restaurants")
            populateRestaurantDetails()
        } else {
            Log.e("DetailsFragment", "Restaurant ID is empty")
        }
    }

    private fun populateRestaurantDetails() {
        val restaurantRef = database.child(restaurantId)
        restaurantRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                if (restaurant != null) {
                    Log.d("DetailsFragment", "Retrieved Restaurant: $restaurant")
                    displayRestaurantDetails(restaurant)
                } else {
                    Log.e("DetailsFragment", "Restaurant data is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetailsFragment", "Database error: ${error.message}")
            }
        })
    }

    private fun displayRestaurantDetails(restaurant: Restaurant) {
        binding.apply {
            addressEditText.text = restaurant.address
            priceEditText.text = restaurant.price
            cuisineEditText.text = restaurant.cuisine
            paymentEditText.text = restaurant.payment
            parkingEditText.text = restaurant.parking
            dressCodeEditText.text = restaurant.dressCode
            descriptionEditText.text = restaurant.description
            phoneNumberEditText.text = restaurant.phone
            startTimeEditText.text = restaurant.startTime
            endTimeEditText.text = restaurant.endTime

            // Additional logs to verify the data
            Log.d("DetailsFragment", "Business Start Time: ${restaurant.startTime}")
            Log.d("DetailsFragment", "Business End Time: ${restaurant.endTime}")
        }
    }
}
