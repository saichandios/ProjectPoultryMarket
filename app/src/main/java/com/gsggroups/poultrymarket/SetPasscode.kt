package com.gsggroups.poultrymarket

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.RetrofitClient
import com.gsggroups.poultrymarket.Model.ApiResponse
import com.gsggroups.poultrymarket.Model.PasscodeRequest


class SetPasscode: AppCompatActivity() {
    private val baseUrl = RetrofitClient.BASE_URL
    private lateinit var phoneEditText: EditText
    private lateinit var passcodeEditText: EditText
    private lateinit var setPinEditText: EditText
    private lateinit var confirmPinEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_passcode)

        phoneEditText = findViewById(R.id.phoneEditText)
        passcodeEditText = findViewById(R.id.passcodeEditText)
        setPinEditText = findViewById(R.id.setPinEditText)
        confirmPinEditText = findViewById(R.id.confirmPinEditText)
        submitButton = findViewById(R.id.Submit)

        val toolbar = findViewById<Toolbar>(R.id.customToolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(Html.fromHtml("<font color='#000000'>Login</font>"))

        submitButton.setOnClickListener {
            hideKeyboard()
            validateInputs()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Fallback to a new view if no current focus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun validateInputs() {
        val phone = phoneEditText.text.toString()
        val otp = passcodeEditText.text.toString()
        val pin = setPinEditText.text.toString()
        val confirmPin = confirmPinEditText.text.toString()

        // Validate phone number
        if (!isValidPhoneNumber(phone)) {
            phoneEditText.error = "Enter a valid phone number"
            return
        }
        // Validate OTP
        if (otp.length != 4) {
            passcodeEditText.error = "Enter a valid 4-digit OTP"
            return
        }
        // Validate passcode
        if (pin.length != 4) {
            setPinEditText.error = "Enter a valid 4-digit PIN"
            return
        }
        // Validate confirm passcode
        if (pin != confirmPin) {
            confirmPinEditText.error = "PINs do not match"
            return
        }

        // If all validations pass, proceed with submission
        submitPasscode(phone, otp, pin)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Simple regex for validating phone number (10 digits)
        return phone.matches(Regex("\\d{10}"))
    }

    private fun submitPasscode(phone: String, otp: String, pin: String) {
        // Create the request body
        val requestBody = PasscodeRequest(phone, otp, pin)

        // Define the URL endpoint for submitting the passcode
        val url = "${baseUrl}setPasscode"

        // Call the API
        ApiHelper.post(
            url = url,
            body = requestBody,
            responseType = ApiResponse::class.java,
            onSuccess = { response ->
                // Handle success
            },
            onFailure = { error ->
                // Handle error
                CustomAlertDialog(this)
                    .setTitle("Update Failed!!")
                    .setDescription(error)
                    .showOkButton(true, "Retry updating") {
                        println("SetPasscode Alert Ok pressed: $error")
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
