package com.example.rormcustomer

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accountAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userName = snapshot.child("name").value.toString()
                    val userEmail = snapshot.child("email").value.toString()

                    binding.customerName.setText(userName)
                    binding.customerEmail.setText(userEmail)
                } else {
                    Log.e("AccountActivity", "User does not exist")
                }
            }.addOnFailureListener { exception ->
                Log.e("AccountActivity", "Error fetching user data", exception)
            }
        }
    }
}
