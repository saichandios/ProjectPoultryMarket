package com.gsggroups.poultrymarket.Employement

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.R

class EmployeDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private lateinit var phone: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var nameTextView: TextView
    private lateinit var shopTextView: TextView
    var roleId: Int = 936


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employe_details)

        // Check for Google Play Services availability
        if (!isGooglePlayServicesAvailable()) {
            showGooglePlayServicesErrorDialog()
            return // Exit if Google Play Services are not available
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment == null) {
            SupportMapFragment.newInstance().also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.map, it)
                    .commit()
            }
        }
        mapFragment?.getMapAsync(this)

        val toolbar = findViewById<Toolbar>(R.id.employer_details_toolbar)
        val whatsappButton = findViewById<ImageButton>(R.id.whatsapp_button)
        roleId = SharedPreferencesManager.getRoleId(this)?.toInt() ?: 936

        val name = findViewById<TextView>(R.id.name)
        val status = findViewById<TextView>(R.id.status)
        val shopName = findViewById<TextView>(R.id.shopName)
        val mobile = findViewById<TextView>(R.id.mobile)
        val hencount = findViewById<TextView>(R.id.hencount)
        val henweight = findViewById<TextView>(R.id.henweight)
        val address = findViewById<TextView>(R.id.address)
        val location = findViewById<TextView>(R.id.location)
        val district = findViewById<TextView>(R.id.district)
        val statename = findViewById<TextView>(R.id.statename)
        val directionButton = findViewById<Button>(R.id.getdirectionsbtn)

        val user = intent.getParcelableExtra<UserModel>("user_data")
        Log.d("EmployeDetails", "Received user data: $user---" + roleId)

        directionButton.setOnClickListener(View.OnClickListener {
            val label = "My Location"
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show()
            }

        })
        user?.let {
            val source = intent.getStringExtra("source") ?: ""
            when (source) {
                "FarmerFragment" -> {
                    shopName.text =
                        "Farm Name: ${it.propertyList.firstOrNull()?.propertyName ?: "Not Available"}"
                }

                "ShopkeeperFragment" -> {
                    shopName.text =
                        "Shop Name: ${it.propertyList.firstOrNull()?.propertyName ?: "Not Available"}"
                }

                "Trader" -> {
                    shopName.text =
                        "Trade / Shop Name: ${it.propertyList.firstOrNull()?.propertyName ?: "Not Available"}"
                }
            }

            latitude = it.propertyList.firstOrNull()?.propertyLat ?: 0.0
            longitude = it.propertyList.firstOrNull()?.propertyLong ?: 0.0

            if (latitude != 0.0 && longitude != 0.0) {
                directionButton.isEnabled = true
            } else {
                directionButton.isEnabled = false
                directionButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
            }


            if (it.colorTemp != "" && it.colorTemp.equals("orange", true)) {
                status.setTextColor(ContextCompat.getColor(this, R.color.orange))
            } else {
                status.setTextColor(ContextCompat.getColor(this, R.color.green))
            }

            status.text = "${it.status}"
            name.text = "Name: ${it.name}"
            mobile.text = "${it.detail}"
            hencount.text = "Hen Count: ${it.henCount} "
            henweight.text = "Hen Weight: ${it.henWeight} Kgs"
            address.text =
                "Address (Landmark): ${it.propertyList.firstOrNull()?.address1 ?: "Not Available"}"
            location.text =
                "Location : ${it.propertyList.firstOrNull()?.address2 ?: "Not Available"}"


            /*  val stateName = DropDownManager.getStateNameById(it.stateID)?.lowercase()
              val districtName =
                  DropDownManager.getDistrictNameByIds(it.stateID, it.districtID)?.lowercase()
              district.text = "District Name: ${districtName}"
              statename.text = "State Name: ${stateName}"
  */


            phone = it.mobileNumber
            latitude = it.propertyList.firstOrNull()?.propertyLat ?: 0.0
            longitude = it.propertyList.firstOrNull()?.propertyLong ?: 0.0

            val stateName = DropDownManager.getStateNameById(it.stateID + 1)?.lowercase()
            val districtName =
                DropDownManager.getDistrictNameByIds(it.stateID + 1, it.districtID)?.lowercase()

            Log.d(
                "EmployeDetails",
                "Resolved: stateID=${it.stateID} -> $stateName, districtID=${it.districtID} -> $districtName"
            )

            statename.text = "State: $stateName"
            district.text = "District: $districtName"


        }




        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Handle the back button in the toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Close the activity and go back to MainActivity
        }


        mobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            startActivity(intent)
        }

        whatsappButton.setOnClickListener {
            openWhatsApp(phone)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        fMap = googleMap

        // Check for Google Play Services availability
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            // Add a marker and move the camera (example location)
            val location = LatLng(latitude, longitude) // Replace with your desired location
            fMap.addMarker(MarkerOptions().position(location).title("Marker in Hyderabad"))
            fMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            ) // Adjust zoom level for better visibility

            // Check for location permission and enable the location layer
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                enableUserLocation()
            } else {
                checkLocationPermission()
            }
        } else {
            // Show error if Google Play Services are not available
            Toast.makeText(this, "Google Play Services not available", Toast.LENGTH_LONG).show()
        }

    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableUserLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            }
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun showGooglePlayServicesErrorDialog() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (googleApiAvailability.isUserResolvableError(resultCode)) {
            googleApiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Google Play Services Required")
                .setMessage("This app requires Google Play Services to function. Please install or update Google Play Services.")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    // Optionally, redirect to the Play Store or show more instructions
                }
                .setCancelable(false)
                .show()
        }
    }


    private fun openWhatsApp(phoneNumber: String) {
        try {
            val uri = Uri.parse("https://wa.me/${phoneNumber.replace("+", "")}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT)
                .show()
        }
    }


    // Handle the back button press in the action bar
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the activity and go back to MainActivity
        return true
    }


}