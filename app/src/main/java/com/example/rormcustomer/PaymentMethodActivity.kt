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

        val currentPaymentMethod = intent.getStringExtra("currentPaymentMethod")

        when (currentPaymentMethod) {
            "Card" -> binding.cardRadioButton.isChecked = true
            "Cash" -> binding.cashRadioButton.isChecked = true
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.cardRadioButton.setOnClickListener {
            returnToOrderSummary("Card")
        }

        binding.cashRadioButton.setOnClickListener {
            returnToOrderSummary("Cash")
        }

        binding.paymentMethodRadioGroup.setOnClickListener {
            val selectedPaymentMethod = when {
                binding.cardRadioButton.isChecked -> "Card"
                binding.cashRadioButton.isChecked -> "Cash"
                else -> null
            }

            if (selectedPaymentMethod != null) {
                returnToOrderSummary(selectedPaymentMethod)
            } else {
                // Handle case where no payment method is selected
                // Optionally show a message or handle the error condition
            }
        }
    }

    private fun returnToOrderSummary(paymentMethod: String) {
        val resultIntent = Intent().apply {
            putExtra("paymentMethod", paymentMethod)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
