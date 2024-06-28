package com.example.rormcustomer.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rormcustomer.AccountActivity
import com.example.rormcustomer.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserName()

        val accountClickListener = View.OnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
        }

        binding.accountLayout.setOnClickListener(accountClickListener)
        binding.accountIcon.setOnClickListener(accountClickListener)
        binding.accountText.setOnClickListener(accountClickListener)
    }

    private fun fetchUserName() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userName = snapshot.child("name").value.toString()
                    binding.customerNameText.text = userName
                } else {
                    Log.e("ProfileFragment", "User does not exist")
                }
            }.addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Error fetching user data", exception)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
