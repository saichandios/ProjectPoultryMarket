package com.gsggroups.poultrymarket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.RetrofitClient
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.LoginRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient


class Login : AppCompatActivity() {
    private val baseUrl = RetrofitClient.BASE_URL
    private lateinit var mobile: EditText
    private lateinit var pin: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var loginButton: Button

    val loader = LoaderUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mobile = findViewById<EditText>(R.id.phoneEditText)
        pin = findViewById<EditText>(R.id.pinEditText)
        forgotPassword = findViewById<TextView>(R.id.forgot_password)
        loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            hideKeyboard()
            validateInput()
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

        val loginRequest = LoginRequest(
            mobileNumber = mobile,
            pin = pin
        )
        val call = ApiClient.retrofit
            .create(ApiService::class.java)
            .loginUser(loginRequest)

        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                SharedPreferencesManager.saveLoginPIN(this, pin)
                SharedPreferencesManager.saveLoginMobileNUmber(this, mobile)

                if (response.isSuccess) {
                    startActivity(Intent(this, Dashboard::class.java))
                    finish()
                    loader.hide()
                    val userDetails = response.item.userID  // This gets the UserDetails object
                    val userPropertId =
                        response.item.propertyList[0].propertyID  // This gets the UserDetails object
                    val roleIDfromLogin = response.item.roleID  // This gets the UserDetails object
                    val batchReadyUpdatedDateTime = response.item.batchReadyUpdatedDateTime
                    val needLoadUpdatedDateTime = response.item.needLoadUpdatedDateTime
                    val goingForLoadUpdatedTime = response.item.goingForLoadUpdatedDateTime
                    val batchReadyBool = response.item.batchReady
                    val needLoadBool = response.item.needLoad
                    val goingForLoad = response.item.goingForLoad

//                    if (userPropertList.isNotEmpty()) {
//                        val firstPropertyId = userPropertList[0].propertyID
//                        SharedPreferencesManager.savePropertyID(this, firstPropertyId)                    }
//                    val userId = userDetails.userID
//                    val roleId = userDetails.roleID
//                    Log.d("Login", "User ID: $userId")

                    //       intent.putExtra("getUserRequest", Gson().toJson(getUserListRequest))
                    SharedPreferencesManager.saveUserID(this, userDetails)
                    SharedPreferencesManager.saveRoleID(this, roleIDfromLogin)
                    SharedPreferencesManager.saveHenCountSubmit(this, "" + response.item.henCount)
                    SharedPreferencesManager.saveHenSizeSubmitBatch(
                        this,
                        "" + response.item.henWeight
                    )
                    SharedPreferencesManager.saveHenCountSubmitGoing(
                        this,
                        "" + response.item.henCount
                    )
                    SharedPreferencesManager.saveHenSizeSubmitGoing(
                        this,
                        "" + response.item.henWeight
                    )
                    SharedPreferencesManager.savePropertyID(this, userPropertId)
                    SharedPreferencesManager.saveStateId(this, response.item.stateID)
                    SharedPreferencesManager.saveDistrictId(this, response.item.districtID)
                    SharedPreferencesManager.saveSignedIn(this, true)
                    SharedPreferencesManager.savePropertyList(this, response.item.propertyList)
                    SharedPreferencesManager.saveUserName(this, response.item.name)
                    SharedPreferencesManager.saveUserForm(
                        this,
                        response.item.propertyList[0].propertyName
                    )
                    SharedPreferencesManager.saveUserFormAddress1(
                        this,
                        response.item.propertyList[0].address1
                    )
                    SharedPreferencesManager.saveUserFormAddress2(
                        this,
                        response.item.propertyList[0].address2
                    )
                    Log.d(
                        "save----------",
                        "" + SharedPreferencesManager.getBatchBoolean(this)
                    )
                    SharedPreferencesManager.saveBatchBoolean(this, batchReadyBool)
                    SharedPreferencesManager.saveNeedBoolean(this, needLoadBool)
                    SharedPreferencesManager.saveGoingBoolean(this, goingForLoad)
//                    if (batchReadyBool) {
//                        SharedPreferencesManager.saveBatchBoolean(this, batchReadyBool)
//                    }
//
//                    if (needLoadBool) {
//                        SharedPreferencesManager.saveNeedBoolean(this, needLoadBool)
//                    }
//                    if (goingForLoad) {
//                        SharedPreferencesManager.saveGoingBoolean(this, goingForLoad)
//                    }

                } else {
                    loader.hide()

                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
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

    override fun onSupportNavigateUp(): Boolean {
        redirectToRegistrationPage()
        return true
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        } else {
            redirectToRegistrationPage()
        }
    }

    private fun shouldAllowBack(): Boolean {
        // You can customize this if needed
        return false
    }

    private fun redirectToRegistrationPage() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
