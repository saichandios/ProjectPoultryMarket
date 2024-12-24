package com.gsggroups.poultrymarket

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.RetrofitClient
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.ApiResponse
import org.json.JSONObject

class Login : AppCompatActivity() {
    private val baseUrl = RetrofitClient.BASE_URL
    private lateinit var mobile: EditText
    private lateinit var pin: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    val loader = LoaderUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mobile = findViewById<EditText>(R.id.phoneEditText)
        pin = findViewById<EditText>(R.id.pinEditText)
        forgotPassword = findViewById<TextView>(R.id.forgot_password)
        loginButton = findViewById<Button>(R.id.loginButton)
        registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            hideKeyboard()
            validateInput()
        }

        registerButton.setOnClickListener {
            hideKeyboard()
            val intent = Intent(this@Login, WelcomeActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            try {
                val intent = Intent(this@Login, SetPasscode::class.java)
                Log.d("Login", "Button clicked")
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("Login", "Error starting activity", e)
            }
        }

        // Set up the custom toolbar
        val toolbar = findViewById<Toolbar>(R.id.customToolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(Html.fromHtml("<font color='#000000'>Registration</font>"))
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Fallback to a new view if no current focus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun validateInput() {
        val phoneNumber = mobile.text.toString().trim()
        val pinInput = pin.text.toString().trim()
        // Validate Phone Number
        if (!isValidPhoneNumber(phoneNumber)) {
            mobile.error = "Invalid phone number. Please enter a valid 10-digit number."
            return
        }
        // Validate PIN
        if (!isValidPin(pinInput)) {
            pin.error = "Invalid PIN. Please enter a 4-digit number."
            return
        }
        // Proceed with login if validations pass
        loginUser(phoneNumber, pinInput)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Check if the phone number is exactly 10 digits and contains only digits
        return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    private fun isValidPin(pin: String): Boolean {
        // Check if the PIN is exactly 4 digits and contains only digits
        return pin.length == 4 && pin.all { it.isDigit() }
    }

    fun loginUser(mobile: String, pin: String) {
        loader.show()
        val url = "${baseUrl}loginUser"
        val params = mapOf("mobile" to mobile, "pin" to pin)

        ApiHelper.get(
            url = url,
            params = params,
            responseType = ApiResponse::class.java,
            onSuccess = { response ->
                loader.hide()
                // Assuming the response is a LoginResponse or a similar data class
                if (response is ApiResponse) {
                    println("Login successful: ${response.message}")
                    // You can store or use the response data here as needed
                } else {
                    println("Unexpected response type")
                }
            },
            onFailure = { error ->
                loader.hide()
                CustomAlertDialog(this)
                    .setTitle("Login Failed!!")
                    .setDescription(error)
                    .showOkButton(true, "OK") {
                        println("Login Alert Ok pressed: $error")
                    }
                    .showCancelButton(false)
                    .show()
            }
        )
    }

    // Handle the back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Handle back navigation
        return true
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        } else {
        }
    }

    // Example condition method
    private fun shouldAllowBack(): Boolean {
        // Replace with your condition
        return false
    }
}
