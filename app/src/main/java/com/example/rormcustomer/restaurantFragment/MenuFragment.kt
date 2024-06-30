package com.example.rormcustomer.restaurantFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rormcustomer.MenuItemInfoActivity
import com.example.rormcustomer.OrderSummaryActivity
import com.example.rormcustomer.adapter.MenuItemAdapter
import com.example.rormcustomer.databinding.FragmentMenuBinding
import com.example.rormcustomer.models.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MenuFragment : Fragment(), MenuItemAdapter.OnItemClickListener {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        val restaurantId = arguments?.getString("RestaurantId")
        if (restaurantId.isNullOrEmpty()) {
            Log.e("MenuFragment", "Restaurant ID is null or empty")
        } else {
            retrieveMenuItem(restaurantId)
        }

        setupFloatingActionButton()

        return binding.root
    }

    private fun setupFloatingActionButton() {
        binding.fab.setOnClickListener {
            val restaurantId = arguments?.getString("RestaurantId")
            Log.d("MenuFragment", "FAB clicked, Restaurant ID: $restaurantId")
            if (restaurantId != null) {
                val intent = Intent(activity, OrderSummaryActivity::class.java).apply {
                    putExtra("restaurantId", restaurantId)
                }
                startActivity(intent)
            } else {
                Log.e("MenuFragment", "Restaurant ID is null")
            }
        }
    }

    private fun retrieveMenuItem(restaurantId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("MenuFragment", "User ID is null")
            return
        }

        Log.d("MenuFragment", "User ID: $userId")

        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("restaurants").child(restaurantId).child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.e("MenuFragment", "No menu data found at path: ${foodRef.path}")
                    return
                }
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    Log.d("MenuFragment", "Retrieved menu item: $menuItem")
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                Log.d("MenuFragment", "Menu items: $menuItems")
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuFragment", "Failed to retrieve menu items: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        if (menuItems.isNotEmpty()) {
            val adapter = MenuItemAdapter(menuItems, requireContext(), this)
            binding.menuItemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.menuItemRecyclerView.adapter = adapter
            Log.d("MenuFragment", "Menu items retrieved and adapter set successfully")
        } else {
            Log.e("MenuFragment", "No menu items found")
        }
    }

    override fun onItemClick(menuItem: MenuItem) {
        val intent = Intent(requireContext(), MenuItemInfoActivity::class.java).apply {
            putExtra("menuItemName", menuItem.foodName)
            putExtra("restaurantId", menuItem.restaurantId)
            putExtra("isEdit", false)  // Assuming default is adding to basket, not editing
        }
        startActivity(intent)
    }

}
