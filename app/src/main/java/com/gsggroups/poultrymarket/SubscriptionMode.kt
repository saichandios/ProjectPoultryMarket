package com.gsggroups.poultrymarket

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SubscriptionMode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_mode)

        val capsule3Months = findViewById<LinearLayout>(R.id.capsule_3_months)
        val capsule1Year = findViewById<LinearLayout>(R.id.capsule_1_year)
        val capsule_1_month = findViewById<LinearLayout>(R.id.capsule_1_month)

        capsule_1_month.setOnClickListener {
            Toast.makeText(this, "1 Month Selected", Toast.LENGTH_SHORT).show()
        }

        capsule3Months.setOnClickListener {
            Toast.makeText(this, "3 Months Selected", Toast.LENGTH_SHORT).show()
        }

        capsule1Year.setOnClickListener {
            Toast.makeText(this, "1 Year Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToPaymentDetails(amount: String) {
        // Create intent to navigate to PaymentDetailsActivity
        val intent = Intent(this, WelcomeRates::class.java)
        intent.putExtra("payment_amount", amount) // Pass amount as extra
        startActivity(intent)
    }
}