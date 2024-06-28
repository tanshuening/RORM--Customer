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

        binding.paymentAppBarLayout.findViewById<ImageView>(R.id.backButton).setOnClickListener {
/*            val intent = Intent(this, OrderSummaryActivity::class.java)
            startActivity(intent)*/
            finish()
        }

        binding.paymentMethodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedPaymentMethod = when (checkedId) {
                R.id.cashRadioButton -> "Cash"
                R.id.cardRadioButton -> "Card"
                else -> ""
            }
            // Pass the selected payment method back to OrderSummaryActivity
            val resultIntent = Intent()
            resultIntent.putExtra("paymentMethod", selectedPaymentMethod)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
