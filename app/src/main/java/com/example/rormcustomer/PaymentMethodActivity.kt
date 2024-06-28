package com.example.rormcustomer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.rormcustomer.databinding.ActivityPaymentMethodBinding

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button click
        binding.paymentAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, OrderSummaryActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle payment method selection
        binding.paymentMethodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.cashRadioButton -> {
                    // Handle cash payment method selected
                }
                R.id.cardRadioButton -> {
                    // Handle card payment method selected
                }
            }
        }
    }
}
