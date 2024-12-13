package com.gsggroups.poultrymarket

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Employement.EmployementDashboard

class WelcomeRates : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_rates)

        val toolbar = findViewById<Toolbar>(R.id.welcomeRatesToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(Html.fromHtml("<font color='#000000'>Welcome</font>"))
        // Handle the back button in the toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Close the activity and go back to MainActivity
        }

        val cardChicken = findViewById<CardView>(R.id.cardChicken)
        val cardEgg = findViewById<CardView>(R.id.cardEgg)

        cardChicken.setOnClickListener {
            val intent = Intent(this, RegisterSingup::class.java)
            intent.putExtra("selectedCard", "rate_card")
            val ratesType = "chicken_card"
            SharedPreferencesManager.saveRatesCard(context = this, role = ratesType)
            startActivity(intent)
        }

        cardEgg.setOnClickListener {
            val intent = Intent(this, RegisterSingup::class.java)
            intent.putExtra("selectedCard", "rate_card")
            val ratesType = "egg_card"
            SharedPreferencesManager.saveRatesCard(context = this, role = ratesType)
            startActivity(intent)
        }
    }

    // Handle the back button press in the action bar
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the activity and go back to MainActivity
        return true
    }
}