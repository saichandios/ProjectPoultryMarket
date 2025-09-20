package com.gsggroups.poultrymarket

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.PropertyRequest
import com.gsggroups.poultrymarket.Model.UserRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.Utils.UserItem
import com.gsggroups.poultrymarket.base.ApiClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfile : Fragment() {

    private lateinit var districts: List<String>
    private lateinit var farmContainer: LinearLayout
    private var farmFieldCount = 0

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var nameEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var pinEditText: EditText
    private lateinit var farmEditText: EditText
    private lateinit var stateSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var address1EditText: EditText
    private lateinit var address2EditText: EditText
    private lateinit var submitButton: Button
    private lateinit var loader: LoaderUtils
    var selectedStatePosition = -1
    var selectedDistrictPosition = -1
    private var locationListener: LocationListener? = null
    var farmLat: Double = 0.0
    var farmLong: Double = 0.0
    var userRoleId: Int = 0
    var userRoleName: String = ""
    private lateinit var imgGPS: ImageView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loader = LoaderUtils(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loader = LoaderUtils(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        farmContainer = view.findViewById(R.id.farm_container)
        val addFarmLink = view.findViewById<TextView>(R.id.link_add_farm)
        nameEditText = view.findViewById(R.id.textfield_name)
        mobileEditText = view.findViewById(R.id.textfield_mobile)
        pinEditText = view.findViewById(R.id.textfield_pin)
        farmEditText = view.findViewById(R.id.textfield_farm)
        stateSpinner = view.findViewById(R.id.spinner_state)
        districtSpinner = view.findViewById(R.id.spinner_district)
        address1EditText = view.findViewById(R.id.textfield_address1)
        address2EditText = view.findViewById(R.id.textfield_address2)
        submitButton = view.findViewById(R.id.button_profile_submit)
        imgGPS = view.findViewById<ImageView>(R.id.icon_gps)

        // Get full list once for reuse

        nameEditText.setText(SharedPreferencesManager.getUserName(requireContext()))
        mobileEditText.setText(SharedPreferencesManager.getLoginMobileNumber(requireContext()))
        pinEditText.setText(SharedPreferencesManager.getLoginPIN(requireContext()))
        farmEditText.setText(SharedPreferencesManager.getUserForm(requireContext()))
        address1EditText.setText(SharedPreferencesManager.getUserFormAdress1(requireContext()))
        address2EditText.setText(SharedPreferencesManager.getUserFormAdress2(requireContext()))
        if (SharedPreferencesManager.getDistrictId(requireContext()) != 0) {
            selectedDistrictPosition = SharedPreferencesManager.getDistrictId(requireContext())
        }
        if (SharedPreferencesManager.getStateId(requireContext()) != 0) {
            selectedStatePosition = SharedPreferencesManager.getStateId(requireContext())
        }
        /*        address2EditText.setText(SharedPreferencesManager.getUserFormAdress2(requireContext()))
                farmLat = SharedPreferencesManager.getFarmLat(requireContext())?.toDouble() ?: 0.0
                farmLong = SharedPreferencesManager.getFarmLong(requireContext())?.toDouble() ?: 0.0
                userRoleId = SharedPreferencesManager.getUserRoleId(requireContext()) ?: 0
                userRoleName = SharedPreferencesManager.getUserRoleName(requireContext()) ?: ""*/
        Log.d("EditProfile", "User Role ID: $userRoleId, Name: $userRoleName")

// Disable editing
//        disableEditText(nameEditText)
        disableEditText(mobileEditText)
        disableEditText(pinEditText)
        disableEditText(address2EditText)


        val savedStateId = SharedPreferencesManager.getStateId(requireContext())
        val savedDistId = SharedPreferencesManager.getDistrictId(requireContext())
        val states = DropDownManager.getStates().drop(1)
        Log.d(
            "get -------- statePosition IDS",
            " savedStateId: $savedStateId, savedDistId: $savedDistId"
        )

        val stateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            states
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        stateSpinner.adapter = stateAdapter

// Set saved state selection (before listener)
        if (savedStateId > 0 && savedStateId <= states.size) {
            stateSpinner.setSelection(savedStateId - 1)
        }

// State listener
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedStatePosition = position + 1
                val selectedState = states[position]
                val districts = DropDownManager.getDistrictsForState(selectedState)

                val districtAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                districtSpinner.adapter = districtAdapter

                // ✅ Only restore saved district if state matches saved state
                if (selectedStatePosition == savedStateId && savedDistId > 0 && savedDistId <= districts.size) {
                    districtSpinner.setSelection(savedDistId - 1, false)
                } else {
                    // ✅ If user changed state → reset district to first item
                    districtSpinner.setSelection(0, false)
                }

                // Attach district listener
                districtSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedDistrictPosition = position + 1
                            Log.d("District Selected", "distId=$savedDistId, position=$position")
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        imgGPS.setOnClickListener {
            loader.show()
            Toast.makeText(requireContext(), "your address loading...", Toast.LENGTH_LONG).show()
            checkLocationPermission()
        }


        submitButton.setOnClickListener {
            handleSubmit(view)
        }

        // Handle click on "Add Farm" link
        addFarmLink.setOnClickListener {
            addNewFarmFields()
        }
    }


    private fun addNewFarmFields() {
        // Increment farm count to differentiate the views
        farmFieldCount++

        // Create a new LinearLayout to hold the new fields
        val newFarmLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
        }

        // Create the "Enter Farm Name" EditText
        val farmNameField = EditText(requireContext()).apply {
            hint = "Enter Farm Name"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }

        // Create the "Enter Address" EditText
        val address1Field = EditText(requireContext()).apply {
            hint = "Enter Address"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }


        // Create the LinearLayout to hold the EditText and ImageView
        val addressLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 0) // Set top margin of 8dp
        }

        // Create the "Enter Address 2" EditText
        val address3Field = EditText(requireContext()).apply {
            id = View.generateViewId() // Generate a unique ID programmatically
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.border_edittext
                ) // Set background
            }
            isEnabled = false // Set to disabled
            gravity = Gravity.TOP // Set gravity to top
            hint = "Enter Address 3" // Set hint
            inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE // Set input type
            setLines(4) // Set the number of lines
        }

        // Create the ImageView
        val gpsIcon = ImageView(requireContext()).apply {
            id = View.generateViewId() // Generate a unique ID programmatically
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END or Gravity.TOP // Align to the end and top
                setMargins(0, -105, 8, 0) // Set margins (top, end)
            }
            setImageResource(android.R.drawable.ic_menu_add) // Set the image resource
        }

        // Create the Delete Button
        val deleteButton = Button(requireContext()).apply {
            text = "Delete"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            ) // Set background color
            setTextColor(Color.WHITE) // Set text color for better visibility
            setOnClickListener {
                farmContainer.removeView(newFarmLayout)
            }
        }

        // Add the fields to the new layout
        newFarmLayout.addView(farmNameField)
        newFarmLayout.addView(address1Field)
        newFarmLayout.addView(address3Field)
        newFarmLayout.addView(gpsIcon)
        newFarmLayout.addView(deleteButton)

        // Add the new layout to the container
        farmContainer.addView(newFarmLayout)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            checkGPSEnabled()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) checkGPSEnabled()
            else {
                loader.hide()
                CustomAlertDialog(requireContext())
                    .setTitle("Location permission is required to fetch GPS coordinates.")
                    .showOkButton(true, "OK") {}.showCancelButton(false).show()
            }
        }

    private val gpsResolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK)
            getLocationAndFillAddress()
        else {
            loader.hide()
            Toast.makeText(requireContext(), "GPS not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkGPSEnabled() {
        val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) { getLocationAndFillAddress() }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    gpsResolutionLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    loader.hide()
                    Toast.makeText(requireContext(), "Unable to resolve GPS settings", Toast.LENGTH_SHORT).show()
                }
            } else {
                loader.hide()
                Toast.makeText(requireContext(), "GPS not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationAndFillAddress() {
        // Explicit permission check
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request again or show message
         //   loader.hide()
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        val cancellationToken = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                farmLat = location.latitude
                farmLong = location.longitude
                getAddressFromLatLong(farmLat, farmLong)
            } else {
            //    loader.hide()
                Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
         //   loader.hide()
            Toast.makeText(requireContext(), "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Timeout to ensure loader hides
        imgGPS.postDelayed({
            if (farmLat == 0.0 && farmLong == 0.0) {
                loader.hide()
                Toast.makeText(requireContext(), "Unable to fetch location, try again.", Toast.LENGTH_SHORT).show()
            }
        }, 15000)
    }

    private fun getAddressFromLatLong(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                address2EditText.setText(addresses[0].getAddressLine(0))
            } else {
                address2EditText.setText(SharedPreferencesManager.getUserFormAdress2(requireContext()))
            }
        } catch (e: IOException) {
            address2EditText.setText("Error getting address")
            e.printStackTrace()
        } finally {
            loader.hide()
            hideKeyboard()
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun handleSubmit(view: View) {
        if (validateInputs()) {
          //  loader.show()

            val name = nameEditText.text.toString().trim()
            val mobile = mobileEditText.text.toString().trim()
            val pin = pinEditText.text.toString().trim()
            val farmName = farmEditText.text.toString().trim()
            val state = stateSpinner.selectedItem?.toString() ?: ""
            val district = districtSpinner.selectedItem?.toString() ?: ""
            val address1 = address1EditText.text.toString().trim()
            val address2 = address2EditText.text.toString().trim()


            val now = getCurrentDateTime()

            val propertyDetails = PropertyRequest(
                propertyID = SharedPreferencesManager.getPropertyId(requireContext()) ?: "",
                userID = SharedPreferencesManager.getUserId(requireContext()) ?: "",
                address1 = address1,
                address2 = address2,
                propertyName = farmName,
                propertyLat = farmLat,
                propertyLong = farmLong,
                isDeleted = false, // or true based on your requirement
                createdDateTime = now,
                updatedDateTime = now
            )

            val userRequest = UserRequest(
                userID = SharedPreferencesManager.getUserId(requireContext()) ?: "",
                name = name,
                stateID = selectedStatePosition,
                districtID = selectedDistrictPosition,
                cityID = 0,
                latitude = farmLat,
                longitude = farmLong,
                batchReady = SharedPreferencesManager.getBatchBoolean(requireContext()),
                needLoad = SharedPreferencesManager.getNeedBoolean(requireContext()),
                goingForLoad = SharedPreferencesManager.getGoingBoolean(requireContext()),
                henCount = 0,
                henWeight = 0.0f,
                mobileNumber = mobile,
                propertyList = listOf(propertyDetails)
            )


            val call =
                ApiClient.retrofit.create(ApiService::class.java).updateUserProfile(userRequest)


            ApiHelper.postNew(endpointCall = call, onSuccess = post@{ response ->
                loader.hide()
                if (response.isSuccess) {

                    val user = response.newItem
                    println("User updated: ${user}")
                    // Save IDs

                    // Update UI with new data
                    updateUiFromResponse(user)
/*
// Move to DashboardActivity
                    val intent = Intent(requireContext(), Dashboard::class.java)

// Optional: pass extra to indicate which menu/tab to open
                    startActivity(intent)
                    requireActivity().finish() // close EditProfile and Dashboard stack if needed
*/

                    Toast.makeText(
                        requireContext(),
                        "Profile updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()


                } else {
                    CustomAlertDialog(requireContext())
                        .setTitle("Request Error")
                        .setDescription("${response.message}")
                        .showOkButton(true, "OK") {
          //                  loader.hide()
                            println("User acknowledged the error.")
                        }
                        .showCancelButton(false)
                        .show()
                    return@post

                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            }, onFailure = { error ->
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun showSnack(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun validateInputs(): Boolean {
        if (nameEditText.text.toString().isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }

        if (mobileEditText.text.toString().isEmpty()) {
            mobileEditText.error = "Mobile number is required"
            return false
        } else if (mobileEditText.text.toString().length != 10) {
            mobileEditText.error = "Mobile number must be 10 digits"
            return false
        }

        if (pinEditText.text.toString().isEmpty()) {
            pinEditText.error = "Pin is required"
            return false
        } else if (pinEditText.text.toString().length != 4) {
            pinEditText.error = "Pin must be 4 digits"
            return false
        }

        if (stateSpinner.selectedItem == null || stateSpinner.selectedItem.toString() == "All" || stateSpinner.selectedItem.toString() == "") {
            Toast.makeText(requireContext(), "Please select a state", Toast.LENGTH_SHORT).show()
            return false
        }

        if (districtSpinner.selectedItem == null || districtSpinner.selectedItem.toString() == "" || districtSpinner.selectedItem.toString() == "All") {
            Toast.makeText(requireContext(), "Please select a district", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (tfCity.selectedItem == null || tfCity.selectedItem.toString() == "" || tfCity.selectedItem.toString() == "All") {
//            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show()
//            return false
//        }

        if (address1EditText.text.toString().isEmpty()) {
            address1EditText.error = "Address 1 is required"
            return false
        }

//        if (tfAdd2.text.toString().isEmpty()) {
//            tfAdd2.error = "Click on GPS button in Address 2"
//            return false
//        }

        return true
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun disableEditText(editText: EditText) {
        editText.isEnabled = false
        editText.isFocusable = false
        editText.isClickable = false
        editText.isCursorVisible = false
        editText.isLongClickable = false
        editText.keyListener = null
    }

    private fun updateUiFromResponse(user: UserItem) {
        // Set text values
        nameEditText.setText(user.name)
        mobileEditText.setText(user.mobileNumber)
        farmEditText.setText(user.propertyList.firstOrNull()?.propertyName ?: "")
        address1EditText.setText(user.propertyList.firstOrNull()?.address1 ?: "")
        address2EditText.setText(user.propertyList.firstOrNull()?.address2 ?: "")

        // Disable editing
        nameEditText.isEnabled = false
        mobileEditText.isEnabled = false
        pinEditText.isEnabled = false
        farmEditText.isEnabled = false
        address1EditText.isEnabled = false
        address2EditText.isEnabled = false
        submitButton.isEnabled=false
        submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
        submitButton.text="Updated"
        SharedPreferencesManager.saveUserID(requireContext(), user.userID)
        SharedPreferencesManager.saveRoleID(requireContext(), user.roleID)
        SharedPreferencesManager.savePropertyID(requireContext(), user.propertyList[0].propertyID)
        SharedPreferencesManager.saveStateId(requireContext(), user.stateID)
        SharedPreferencesManager.saveDistrictId(requireContext(), user.districtID)
        SharedPreferencesManager.savePropertyList(requireContext(), user.propertyList)
        SharedPreferencesManager.saveUserName(requireContext(), user.name)
        SharedPreferencesManager.saveUserForm(requireContext(), user.propertyList[0].propertyName)
        SharedPreferencesManager.saveUserFormAddress2(
            requireContext(),
            user.propertyList[0].address2
        )
        SharedPreferencesManager.saveUserFormAddress1(
            requireContext(),
            user.propertyList[0].address1
        )
        refreshSpinners()

        stateSpinner.isEnabled = false
        districtSpinner.isEnabled = false
        imgGPS.isEnabled=false
    }


    private fun refreshSpinners() {
        val savedStateId = SharedPreferencesManager.getStateId(requireContext())
        val savedDistId = SharedPreferencesManager.getDistrictId(requireContext())
        val states = DropDownManager.getStates().drop(1)
        Log.d(
            "get -------- statePosition IDS",
            " savedStateId: $savedStateId, savedDistId: $savedDistId"
        )

        val stateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            states
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        stateSpinner.adapter = stateAdapter

// Set saved state selection (before listener)
        if (savedStateId > 0 && savedStateId <= states.size) {
            stateSpinner.setSelection(savedStateId - 1)
        }

// State listener
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedStatePosition = position + 1
                val selectedState = states[position]
                val districts = DropDownManager.getDistrictsForState(selectedState)

                val districtAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                districtSpinner.adapter = districtAdapter

                // ✅ Set saved district before adding listener
                if (savedDistId > 0 && savedDistId <= districts.size) {
                    districtSpinner.setSelection(
                        savedDistId - 1,
                        false
                    ) // false = don’t trigger listener
                }

                // Attach district listener
                districtSpinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedDistrictPosition = position + 1
                            Log.d("District Selected", "distId=$savedDistId, position=$position")
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }


}