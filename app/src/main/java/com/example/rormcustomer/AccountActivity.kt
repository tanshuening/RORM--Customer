package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityAccountBinding
import com.example.rormcustomer.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.accountAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        currentUser = auth.currentUser!!

        fetchUserData()

        binding.saveButton.setOnClickListener {
            val newName = binding.customerName.text.toString().trim()

            if (newName.isNotEmpty()) {
                updateUserName(newName)
            }
        }
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

    private fun updateUserName(newName: String) {
        val userId = currentUser.uid
        val userRef = database.child("users").child(userId)

        userRef.child("name").setValue(newName)
            .addOnSuccessListener {
                Log.d("AccountActivity", "User name updated successfully")

                // Update UI after successful update
                binding.customerName.setText(newName)

                // Pass updated name to ProfileFragment
                val intent = Intent(this, ProfileFragment::class.java)
                intent.putExtra("newName", newName)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Log.e("AccountActivity", "Error updating user name", exception)
            }
    }
}
