package com.gsggroups.poultrymarket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.RetrofitClient
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.PasscodeRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient


class SetPasscode : AppCompatActivity() {
    private lateinit var mobile: String
    private lateinit var pin: String
    private lateinit var newPin: String
    private val baseUrl = RetrofitClient.BASE_URL
    private lateinit var phoneEditText: EditText
    private lateinit var passcodeEditText: EditText
    private lateinit var setPinEditText: EditText
    private lateinit var confirmPinEditText: EditText
    private lateinit var submitButton: Button
    val loader = LoaderUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_passcode)

        phoneEditText = findViewById(R.id.phoneEditText)
//        passcodeEditText = findViewById(R.id.passcodeEditText)
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
            loader.show()
            if (validateInputs()) {
                changePasscode(intent)

            }else{
                showCustomdailogEmptyValues(this)
            }
        }
    }

    private fun changePasscode(intent: Intent?) {
            mobile = phoneEditText.text.toString()
            pin = setPinEditText.text.toString()
            newPin = confirmPinEditText.text.toString()

            val passcodeRequest = PasscodeRequest(
                phone = mobile,
                pin = pin,
                newPin = newPin
            )
            // Define the URL endpoint for submitting the passcode
            val call = ApiClient.retrofit
                .create(ApiService::class.java)
                .changePin(passcodeRequest)
            ApiHelper.post(
                endpointCall = call,
                onSuccess = { response ->
                    if (response.isSuccess) {
                        loader.hide()

                        val user = response.item
                        println("User Registered: ${user}")
                        startActivity(Intent(this, Login::class.java))
                        finish()
                        Toast.makeText(this, "Passcode changed successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else if (response.message == "User not found") {
                        showCustomdailogResponseValues(this, response.message)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    } else if (response.message == "Invalid OTP") {
                        showCustomdailogResponseValues(this, response.message)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        showCustomdailogResponseValues(this, response.message)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { error ->
                    showCustomdailogResponseValues(this, error.toString())
                    Toast.makeText(this, "Error: ${error.toString()}", Toast.LENGTH_SHORT).show()
                }
            )

    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Fallback to a new view if no current focus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun validateInputs(): Boolean {
        val phone = phoneEditText.text.toString()
//        val otp = passcodeEditText.text.toString()
        val pin = setPinEditText.text.toString()
        val confirmPin = confirmPinEditText.text.toString()

        // Validate phone number
        if (!isValidPhoneNumber(phone)) {
            phoneEditText.error = "Enter a valid phone number"
            return false
        }
//        // Validate OTP
//        if (otp.length != 4) {
//            passcodeEditText.error = "Enter a valid 4-digit OTP"
//            return
//        }
        // Validate passcode
        if (pin.length != 4) {
            setPinEditText.error = "Enter a valid 4-digit PIN"
            return false
        }
        //  Validate confirm passcode
        if (confirmPin.length != 4) {
            confirmPinEditText.error = "Enter a valid 4-digit PIN"
            return false
        }
        return true

        // If all validations pass, proceed with submission
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Simple regex for validating phone number (10 digits)
        return phone.matches(Regex("\\d{10}"))
    }

    private fun submitPasscode(phone: String, pin: String, confirmPin: String) {
        // Create the request body
        val requestBody = PasscodeRequest(phone, pin, confirmPin)


        /*     // Call the API
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
             )*/
    }

    private fun showCustomdailogEmptyValues(setPasscode: SetPasscode) {
        CustomAlertDialog(setPasscode)
            .setTitle("Empty fields!!")
            .setDescription("Fill all the fields")
            .showOkButton(true, "OK") {
                println("Register")
                loader.hide()
            }
            .showCancelButton(false)
            .show()
    }

    private fun showCustomdailogResponseValues(setPasscode: SetPasscode, error: String) {
        CustomAlertDialog(setPasscode)
            .setTitle("Request Error")
            .setDescription(error)
            .showOkButton(true, "OK") {
                println("Register")
                loader.hide()
            }
            .showCancelButton(false)
            .show()
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
