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
import android.location.LocationListener
import android.view.inputmethod.InputMethodManager
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Model.Property
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Employement.EmployementDashboard
import com.gsggroups.poultrymarket.Model.ApiResponse
import com.gsggroups.poultrymarket.Model.PropertyRequest
import com.gsggroups.poultrymarket.Model.UserItem
import com.gsggroups.poultrymarket.Model.UserRequest
import java.text.SimpleDateFormat
import java.util.Date


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

    private var locationListener: LocationListener? = null
    var farmLat: Double = 0.0
    var farmLong: Double = 0.0


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
        val userRole = SharedPreferencesManager.getUserRole(this)
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
                    when (userRole) {
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
                        "ChicksSupplier" -> {

                        }
                        "Farmer" -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (!validateInputs()) {
//                                SharedPreferencesManager.saveSignedIn(this, true)
                                SharedPreferencesManager.saveUserRole(this,"Farmer")
//                                registerUser(intent)
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
                        "Trader" -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (!validateInputs()) {
//                                SharedPreferencesManager.saveSignedIn(this, true)
                                SharedPreferencesManager.saveUserRole(this,"Trader")
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
                        "Shopkeeper" -> {
                            val intent = Intent(this, Dashboard::class.java)
                            if (!validateInputs()) {
//                                SharedPreferencesManager.saveSignedIn(this, true)
                                SharedPreferencesManager.saveUserRole(this,"Shopkeeper")
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
                        "EmployerOrg" -> {

                        }
                        "Employee" -> {
                            val intent = Intent(this, EmployementDashboard::class.java)
                            if (validateInputs()) {
                                SharedPreferencesManager.saveSignedIn(this, true)
                                SharedPreferencesManager.saveUserRole(context = this, "Employee")
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
                            val intent = Intent(this, Dashboard::class.java)
                            if (validateInputs()) {
                                SharedPreferencesManager.saveSignedIn(this, true)
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
            imgGPS.setOnClickListener {
                loader.show()
                Toast.makeText(this, "your address loading...", Toast.LENGTH_LONG).show()
                checkLocationPermission()
            }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this) // Fallback to a new view if no current focus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

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

        val userRole = SharedPreferencesManager.getUserRole(this)
        when (userRole) {
            "Employee" -> {
                tfAdd2.hint = "Click on GPS button in this box for your current location"
                tfFarm.visibility = View.GONE
            }
            "Trader" -> {
                tfFarm.hint = "Enter Your Shop Name"
            }
            "Shopkeeper" -> {
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


        // Populate States
        val stateAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            DropDownManager.getStates()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        tfState.adapter = stateAdapter

        // Listen for state selection
        tfState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedState = parent.getItemAtPosition(position).toString()

                // Populate Districts based on selected State
                val districtAdapter = ArrayAdapter(
                    this@RegisterSingup,
                    android.R.layout.simple_spinner_item,
                    DropDownManager.getDistrictsForState(selectedState)
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                tfDistrict.adapter = districtAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // State adapter setup
        tfState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, DropDownManager.getStates())
        tfState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedState = DropDownManager.getStates()[position]
                val districts = DropDownManager.getDistrictsForState(selectedState)
                tfDistrict.adapter = ArrayAdapter(this@RegisterSingup, android.R.layout.simple_spinner_item, districts)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // District adapter setup
        tfDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDistrict = tfDistrict.selectedItem.toString()
                val cities = citiesMap[selectedDistrict] ?: emptyList()
//                tfCity.adapter = ArrayAdapter(this@RegisterSingup, android.R.layout.simple_spinner_item, cities)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun validateInputs(): Boolean {
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            checkGPSEnabled()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            checkGPSEnabled()
        } else {
            // Permission denied
             CustomAlertDialog(this)
                .setTitle("Location permission is required to fetch GPS coordinates.")
                .setDescription("")
                .showOkButton(true, "OK") {
                }
                .showCancelButton(false)
                .show()
        }
    }

    private fun checkGPSEnabled() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlert()
        } else {
            getLocationAndFillAddress()
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
        val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                println(addresses[0].locality)
                val address = addresses[0].getAddressLine(0)
                tfAdd2.setText(address)
                loader.hide()
                hideKeyboard()
                // Optionally, you can stop GPS updates here too, in case the address retrieval is not immediate
                locationListener?.let {
                    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    locationManager.removeUpdates(it)
                }
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
                address1 = address1,  // Address line 1
                address2 = address2,  // Address line 2
                propertyName = farmName,  // Farm name
                propertyLat = 0.0,  // Latitude (provide the actual value)
                propertyLong = 0.0,  // Longitude (provide the actual value)
                createdDateTime = "",  // Sample creation date (adjust as needed)
                updatedDateTime = "",  // Sample update date (adjust as needed)
                isDeleted = false  // Set as per your logic
            )

            val propertyList = listOf(propertyDetails)

            val userRequest = UserRequest(
                name = name,
                mobileNumber = mobile,
                pin = pin,
                stateID = state,
                districtID = district,
                cityID = "",  // If you want to get the city, use a similar method to map
                latitude = farmLat,  // Assuming you might get the location using a GPS API
                longitude = farmLong,  // Same as latitude
                batchReady = false,  // Set as per your condition
                needLoad = false,  // Set as per your condition
                goingForLoad = false,  // Set as per your condition
                henCount = 0,  // Set as per your logic
                henWeight = 0.0f,  // Set hen weight if needed
                deviceToken = "",  // Provide device token if applicable
                roleID = 0,  // Assigned role ID
                propertyList = propertyList,  // Adding the property to the property list
                subscriptionID = 0,  // Subscription ID if applicable
                isDeleted = false,  // Set according to your logic
                createdDateTime = "",  // Current timestamp
                updatedDateTime = "",  // Current timestamp
            )

            // Call the API
            ApiHelper.post(
                url = "registerUser",
                body = userRequest,
                responseType = ApiResponse::class.java,
                onSuccess = { apiResponse ->
                    if (apiResponse.isSuccess) {
                        val userItem = apiResponse.item
                        println("User Name: ${userItem.name}")
                        // Save user data
                        SharedPreferencesManager.saveUserData(this, userItem)
                        val userData = SharedPreferencesManager.getUserData(this)
                        userData?.let {
                            println("Retrieved User Data: ${userData.name}")
                        }
                        // Navigate or handle further actions
                        startActivity(intent)
                    } else {
                        CustomAlertDialog(this)
                            .setTitle("Error")
                            .setDescription(apiResponse.message)
                            .showOkButton(true, "OK") {
                                println("User acknowledged the error.")
                            }
                            .showCancelButton(false)
                            .show()
                    }
                },
                onFailure = { error ->
                    CustomAlertDialog(this)
                        .setTitle("Registration Failed")
                        .setDescription(error)
                        .showOkButton(true, "Retry") {
                            println("Retrying registration...")
                        }
                        .showCancelButton(false)
                        .show()
                }
            )
        }
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
}