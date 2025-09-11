package com.gsggroups.poultrymarket

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import java.util.Locale
import android.Manifest
import android.content.IntentSender
import android.location.LocationListener
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Model.Property
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.Employement.EmployementDashboard
import com.gsggroups.poultrymarket.Model.ApiResponse
import com.gsggroups.poultrymarket.Model.PropertyRequest
import com.gsggroups.poultrymarket.Model.UserItem
import com.gsggroups.poultrymarket.Model.UserRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class RegisterSingup: AppCompatActivity() {
    private lateinit var roleSpinner: Spinner
    private lateinit var tfName: EditText
    private lateinit var tfMobile: EditText
    private lateinit var tfPin: EditText
    private lateinit var tfFarm: EditText
    private lateinit var tfState: Spinner
    private lateinit var tfDistrict: Spinner
//    private lateinit var tfCity: Spinner
    private lateinit var tfAdd1: EditText
    private lateinit var tfAdd2: EditText
    private lateinit var imgGPS: ImageView
    private lateinit var registerButton: Button

    val loader = LoaderUtils(this)
    private var isWaitingForGPS = false

    private var locationListener: LocationListener? = null
    var farmLat: Double = 0.0
    var farmLong: Double = 0.0
    var userRoleId: Int = 0
    var userRoleName: String = ""

    private var citiesMap = mapOf(
        "District1" to listOf("City1", "City2"),
        "District2" to listOf("City3", "City4"),
        "District3" to listOf("City5", "City6"),
        "District4" to listOf("City7", "City8")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        val toolbar = findViewById<Toolbar>(R.id.customToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(Html.fromHtml("<font color='#000000'>Welcome</font>"))
        // Handle the back button in the toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Close the activity and go back to MainActivity
        }
        userRoleName = SharedPreferencesManager.getUserRole(this).toString()
        userRoleId = UserRoles.getRoleIdByName(userRoleName ?: "").toInt()
        val selectedRateCard = SharedPreferencesManager.getRatesCard(this)

        setupView()

        // Find the button and set an onClick listener
        val loginButton = findViewById<Button>(R.id.button_login)
        loginButton.setOnClickListener {
            hideKeyboard()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

                registerButton.setOnClickListener{
                    hideKeyboard()
                    when (userRoleName) {
                        "Rates" -> {
                            when (selectedRateCard) {
                                "chicken_card" -> {
                                    if (!validateInputs()) {
                                        val intent = Intent(this, RateChicken_Activity::class.java)
                                        startActivity(intent)
                                    } else {
                                        CustomAlertDialog(this)
                                            .setTitle("Empty fields!!")
                                            .setDescription("Fill all the fields")
                                            .showOkButton(true, "OK") {
                                                println("Register")
                                            }
                                            .showCancelButton(false)
                                            .show()
                                    }
                                }
                                else -> {
                                    if (!validateInputs()) {
                                        val intent = Intent(this, RateEgg_Activity::class.java)
                                        startActivity(intent)
                                    } else {
                                        CustomAlertDialog(this)
                                            .setTitle("Empty fields!!")
                                            .setDescription("Fill all the fields")
                                            .showOkButton(true, "OK") {
                                                println("Register")
                                            }
                                            .showCancelButton(false)
                                            .show()
                                    }
                                }
                            }
                        }
                        UserRoles.ROLE_CHICKS_SUPPLIER -> {

                        }
                        UserRoles.ROLE_FARMER -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (validateInputs()) {
                                registerUser(intent)
//                                startActivity(intent)
                            } else {
                                showCustomdailogEmptyValues(this)
                            }
                        }
                        UserRoles.ROLE_TRADER -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (validateInputs()) {
                                registerUser(intent)
                            } else {
                                showCustomdailogEmptyValues(this)
                            }
                        }
                        UserRoles.ROLE_SHOPKEEPER -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (validateInputs()) {
                                registerUser(intent)
                            } else {
                                showCustomdailogEmptyValues(this)
                            }
                        }
                        UserRoles.ROLE_EMPLOYERORG -> {

                        }
                        UserRoles.ROLE_EMPLOYEE -> {
                            val intent = Intent(this, EmployementDashboard::class.java)
                            if (validateInputs()) {
//                                SharedPreferencesManager.saveSignedIn(this, true)
//                                registerUser(intent)
                            } else {
                                showCustomdailogEmptyValues(this)
                            }
                        }
                        else -> {
                                CustomAlertDialog(this)
                                    .setTitle("Try again after some time")
                                    .setDescription("Something went wrong from App")
                                    .showOkButton(true, "OK") {
                                        println("Register")
                                    }
                                    .showCancelButton(false)
                                    .show()
                        }
                    }

                }
            imgGPS.setOnClickListener {
                loader.show()
                Toast.makeText(this, "your address loading...", Toast.LENGTH_LONG).show()
                checkLocationPermission()
            }
    }

    private fun showCustomdailogEmptyValues(registerSingup: RegisterSingup) {
        CustomAlertDialog(registerSingup).setTitle("Empty fields!!")
            .setDescription("Fill all the fields").showOkButton(true, "OK") {
                println("Register")
                loader.hide()
            }.showCancelButton(false).show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Fallback to a new view if no current focus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    var selectedStatePosition = -1
    var selectedDistrictPosition = -1
    fun setupView() {
        roleSpinner = findViewById<Spinner>(R.id.spinner_role)
        tfName = findViewById<EditText>(R.id.textfield_name)
        tfMobile = findViewById<EditText>(R.id.textfield_mobile)
        tfPin = findViewById<EditText>(R.id.textfield_pin)
        tfFarm = findViewById<EditText>(R.id.textfield_farm)
        tfState = findViewById<Spinner>(R.id.spinner_state)
        tfDistrict = findViewById<Spinner>(R.id.spinner_district)
//        tfCity = findViewById<Spinner>(R.id.spinner_City)
        tfAdd1 = findViewById<EditText>(R.id.textfield_address1)
        tfAdd2 = findViewById<EditText>(R.id.textfield_address2)
        imgGPS = findViewById<ImageView>(R.id.icon_gps)
        registerButton = findViewById<Button>(R.id.button_register)

        val receivedValue = intent.getStringExtra("selectedCard")
        if (receivedValue == "rate_card") {
            roleSpinner.visibility = View.VISIBLE
        } else {
            roleSpinner.visibility = View.GONE
        }

        when (userRoleName) {
            UserRoles.ROLE_EMPLOYEE -> {
                tfAdd2.hint = "Click on GPS button in this box for your current location"
                tfFarm.visibility = View.GONE
            }
            UserRoles.ROLE_TRADER -> {
                tfFarm.hint = "Enter Your Shop Name"
            }
            UserRoles.ROLE_SHOPKEEPER -> {
                tfFarm.hint = "Enter Your Shop Name"
            }
            "Rates" -> {
                tfFarm.hint = "Enter Farm/Shop Name"
            }
            else -> {
                tfFarm.hint = "Enter Farm Name"
                tfAdd2.hint = "Go to Hen Farm location and click on GPS button in this box"
                tfFarm.visibility = View.VISIBLE
            }
        }

        // Get full list once for reuse
        val states = DropDownManager.getStates().drop(1)


// Adapter for states
        val stateAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, states
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        tfState.adapter = stateAdapter



        tfState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store selected state position
                selectedStatePosition = position + 1

                // Get the districts for this state
                val selectedState = states[position]
                Log.d("RegisterSingup", "Selected State: $selectedState")
                val districts = DropDownManager.getDistrictsForState(selectedState)

                // Set up district adapter
                val districtAdapter = ArrayAdapter(
                    this@RegisterSingup, android.R.layout.simple_spinner_item, districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                tfDistrict.adapter = districtAdapter

                // Reset district position
                selectedDistrictPosition = -1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

// District selection listener
        tfDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store district position
                selectedDistrictPosition = position+1
                Log.d("RegisterSingup", "Selected dist: $selectedStatePosition")

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun validateInputs(): Boolean {
        println("validateInputs called" + tfName.text.toString() + "\n" + tfMobile.text.toString() + "\n" + tfPin.text.toString() + "\n" + tfFarm.text.toString() + "\n" + tfState.selectedItem.toString() + "\n" + tfDistrict.selectedItem.toString() + "\n" + tfAdd1.text.toString() + "\n" + tfAdd2.text.toString())
        if (tfName.text.toString().isEmpty()) {
            tfName.error = "Name is required"
            return false
        }

        if (tfMobile.text.toString().isEmpty()) {
            tfMobile.error = "Mobile number is required"
            return false
        } else if (tfMobile.text.toString().length != 10) {
            tfMobile.error = "Mobile number must be 10 digits"
            return false
        }

        if (tfPin.text.toString().isEmpty()) {
            tfPin.error = "Pin is required"
            return false
        } else if (tfPin.text.toString().length != 4) {
            tfPin.error = "Pin must be 4 digits"
            return false
        }

        if (tfState.selectedItem == null || tfState.selectedItem.toString() == "All" || tfState.selectedItem.toString() == "") {
            Toast.makeText(this, "Please select a state", Toast.LENGTH_SHORT).show()
            return false
        }

        if (tfDistrict.selectedItem == null || tfDistrict.selectedItem.toString() == "" || tfDistrict.selectedItem.toString() == "All") {
            Toast.makeText(this, "Please select a district", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (tfCity.selectedItem == null || tfCity.selectedItem.toString() == "" || tfCity.selectedItem.toString() == "All") {
//            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show()
//            return false
//        }

        if (tfAdd1.text.toString().isEmpty()) {
            tfAdd1.error = "Address 1 is required"
            return false
        }

//        if (tfAdd2.text.toString().isEmpty()) {
//            tfAdd2.error = "Click on GPS button in Address 2"
//            return false
//        }

        return true
    }


    //GPS check and get physical address
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            checkGPSEnabled()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                checkGPSEnabled()
            } else {
                // Permission denied
                CustomAlertDialog(this).setTitle("Location permission is required to fetch GPS coordinates.")
                    .setDescription("").showOkButton(true, "OK") {}.showCancelButton(false).show()
            }
        }

    private fun checkGPSEnabled() {
        val locationRequest =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true) // 👈 this triggers dialog

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // All good — GPS already enabled
            getLocationAndFillAddress()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, 1001)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else {
                loader.hide()
                Toast.makeText(this, "GPS not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGPSDisabledAlert() {
             CustomAlertDialog(this)
            .setTitle("\"GPS is disabled. Please enable GPS to use this feature.\"")
            .setDescription("Fill all the fields")
            .showOkButton(true, "Enable GPS") {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .showCancelButton(true, "Cancel")
            .show()
    }

    private fun getLocationAndFillAddress() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    farmLat = latitude
                    farmLong = longitude
                    getAddressFromLatLong(latitude, longitude)
                    // Stop location updates after receiving the address
                    locationManager.removeUpdates(this)
                }

                // Optional: Override other methods for better performance
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener!!)
        }
    }

    private fun getAddressFromLatLong(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                tfAdd2.setText(address)
            } else {
                tfAdd2.setText("Address not found")
            }
        } catch (e: IOException) {
            tfAdd2.setText("Error getting address")
            e.printStackTrace()
        } finally {
            loader.hide()
            hideKeyboard()

            // Just in case — remove updates again here
            locationListener?.let {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.removeUpdates(it)
            }
        }
    }

    fun registerUser(intent: Intent) {
        if (validateInputs()) {
            val role = roleSpinner.selectedItem.toString()
            val name = tfName.text.toString()
            val mobile = tfMobile.text.toString()
            val pin = tfPin.text.toString()
            val farmName = tfFarm.text.toString()
            val state = tfState.selectedItem.toString()
            val district = tfDistrict.selectedItem.toString()
            val address1 = tfAdd1.text.toString()
            val address2 = tfAdd2.text.toString()

            val propertyDetails = PropertyRequest(
                address1 = address1,
                address2 = address2,
                propertyName = farmName,
                propertyLat = farmLat,
                propertyLong = farmLong,
                createdDateTime = getCurrentDateTime(),
                updatedDateTime = getCurrentDateTime(),
                isDeleted = false
            )

            val propertyList = listOf(propertyDetails)

            val userRequest = UserRequest(
                name = name,
                mobileNumber = mobile,
                pin = pin,
                stateID = selectedStatePosition,
                districtID = selectedDistrictPosition,
                cityID = 0,
                latitude = farmLat,
                longitude = farmLong,
                batchReady = false,
                needLoad = false,
                goingForLoad = false,
                henCount = 0,
                henWeight = 0.0f,
                deviceToken = "",
                roleID = userRoleId,
                propertyList = propertyList,
                subscriptionID = 0,
                isDeleted = false,
                createdDateTime = getCurrentDateTime(),
                updatedDateTime = getCurrentDateTime(),
                //  static
                userID = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )

            val call = ApiClient.retrofit.create(ApiService::class.java).registerUser(userRequest)

            ApiHelper.postNew(endpointCall = call, onSuccess = { response ->
                if (response.isSuccess) {
                    val user = response.newItem
                    println("User Registered: ${user}")
                 //   loader.hide()

                    startActivity(intent)
                    finish()
                    SharedPreferencesManager.saveSignedIn(this, true)
                    SharedPreferencesManager.saveLoginPIN(this, pin)
                    SharedPreferencesManager.saveLoginMobileNUmber(this, mobile)

//                    val batchReadyUpdatedDateTime = response.newItem.batchReadyUpdatedDateTime
//                    val needLoadUpdatedDateTime = response.newItem.needLoadUpdatedDateTime
//                    val goingForLoadUpdatedTime = response.newItem.goingForLoadUpdatedDateTime
                    val batchReadyBool = response.newItem.batchReady
                    val needLoadBool = response.newItem.needLoad
                    val goingForLoad = response.newItem.goingForLoad

                    SharedPreferencesManager.saveUserID(this, user.userID)
                    SharedPreferencesManager.saveRoleID(this, user.roleID)
//                    SharedPreferencesManager.savePropertyID(this, user.properties[0].propertyID)
//                    if (batchReadyUpdatedDateTime != null && batchReadyBool) {
//                        SharedPreferencesManager.saveLastSubmitTimeBatch(this, batchReadyUpdatedDateTime)
//                    } else {
//                        SharedPreferencesManager.saveLastSubmitTimeBatch(this, "0")
//                    }
//
//                    if (needLoadUpdatedDateTime != null && needLoadBool) {
//                        SharedPreferencesManager.saveLastSubmitTimeNeed(this, needLoadUpdatedDateTime)
//                    } else {
//                        SharedPreferencesManager.saveLastSubmitTimeNeed(this, "0")
//                    }

                } else {
                    showCustomdailogResponseValues(this, response.message)
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }, onFailure = { error ->
                showCustomdailogResponseValues(this, error.toString())
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun showCustomdailogResponseValues(registerSingup: RegisterSingup, error: String) {
        CustomAlertDialog(registerSingup).setTitle("Request Error").setDescription(error)
            .showOkButton(true, "OK") {
                println("Register")
                loader.hide()
            }.showCancelButton(false).show()
    }

    // Handle the back button press in the action bar
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the activity and go back to MainActivity
        return true
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed()
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

    // Example condition method
    private fun shouldAllowBack(): Boolean {
        // Replace with your condition
        return false
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                getLocationAndFillAddress()
            } else {
                loader.hide()
                Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}