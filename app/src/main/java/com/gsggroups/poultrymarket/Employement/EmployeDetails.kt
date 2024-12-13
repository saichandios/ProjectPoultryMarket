package com.gsggroups.poultrymarket.Employement

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.gsggroups.poultrymarket.R

class EmployeDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private lateinit var nameTextView: TextView
    private lateinit var shopTextView: TextView


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
        val mobile = findViewById<TextView>(R.id.mobile)
        val whatsappButton = findViewById<ImageButton>(R.id.whatsapp_button)


        val name = intent.getStringExtra("name")
        val shopName = intent.getStringExtra("role")
        val detailData = intent.getStringExtra("detail")

        findViewById<TextView>(R.id.name).text = "Name: $name"
        findViewById<TextView>(R.id.shopName).text = "Shop Name: $shopName"

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Handle the back button in the toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Close the activity and go back to MainActivity
        }


        // Mobile click to call
        val phone = "+1234567890"
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
            val location = LatLng(17.4065, 78.4772) // Replace with your desired location
            fMap.addMarker(MarkerOptions().position(location).title("Marker in Hyderabad"))
            fMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)) // Adjust zoom level for better visibility

            // Check for location permission and enable the location layer
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            == PackageManager.PERMISSION_GRANTED) {
            fMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableUserLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show()
        }
    }


    // Handle the back button press in the action bar
    override fun onSupportNavigateUp(): Boolean {
        finish() // Close the activity and go back to MainActivity
        return true
    }


}