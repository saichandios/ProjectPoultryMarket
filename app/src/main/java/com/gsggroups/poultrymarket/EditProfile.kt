package com.gsggroups.poultrymarket

import android.content.Intent
import android.graphics.Color
import android.location.LocationListener
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Model.PropertyRequest
import com.gsggroups.poultrymarket.Model.UserRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient
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
    var selectedStatePosition = -1
    var selectedDistrictPosition = -1
    private var locationListener: LocationListener? = null
    var farmLat: Double = 0.0
    var farmLong: Double = 0.0
    var userRoleId: Int = 0
    var userRoleName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        // Get full list once for reuse
        val states = DropDownManager.getStates().drop(1)


// Adapter for states
     /*   val stateAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, states
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        stateSpinner.adapter = stateAdapter



        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    requireContext(), android.R.layout.simple_spinner_item, districts
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                districtSpinner.adapter = districtAdapter

                // Reset district position
                selectedDistrictPosition = -1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

// District selection listener
        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                // Store district position
                selectedDistrictPosition = position+1
                Log.d("RegisterSingup", "Selected dist: $selectedStatePosition")

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Handle submit
        submitButton.setOnClickListener {
            handleSubmit(view)
        }




     */   // Handle click on "Add Farm" link
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
                background = ContextCompat.getDrawable(requireContext(), R.drawable.border_edittext) // Set background
            }
            isEnabled = false // Set to disabled
            gravity = Gravity.TOP // Set gravity to top
            hint = "Enter Address 3" // Set hint
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE // Set input type
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
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red)) // Set background color
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

            val name = nameEditText.text.toString().trim()
            val mobile = mobileEditText.text.toString().trim()
            val pin = pinEditText.text.toString().trim()
            val farmName = farmEditText.text.toString().trim()
            val state = stateSpinner.selectedItem?.toString() ?: ""
            val district = districtSpinner.selectedItem?.toString() ?: ""
            val address1 = address1EditText.text.toString().trim()
            val address2 = address2EditText.text.toString().trim()

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


           /* ApiHelper.postNew(endpointCall = call, onSuccess = { response ->
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
            })*/
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

}