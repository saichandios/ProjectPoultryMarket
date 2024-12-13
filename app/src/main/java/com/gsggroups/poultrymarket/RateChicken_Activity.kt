package com.gsggroups.poultrymarket

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.loader.content.Loader
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Model.DistrictRates
import com.gsggroups.poultrymarket.Model.RatesResponse
import com.gsggroups.poultrymarket.Model.State

class RateChicken_Activity : AppCompatActivity() {
    private lateinit var stateLbl: TextView
    val allDistrictRates = mutableListOf<DistrictRates>()
    private var allStatesWithRates: List<State> = listOf()
    val allStates: MutableList<State> = mutableListOf()
    private val loader = LoaderUtils(this)

    private val stateImages = listOf(
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.hen3
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_chicken)


        val stateButtonsLayout = findViewById<LinearLayout>(R.id.stateButtonsLayoutAct)
        val cardContainer = findViewById<LinearLayout>(R.id.chickenCardContainerLayout)
        stateLbl = findViewById(R.id.stateNameLabel)

        fetchDistrictRates()
        val stateList = DropDownManager.getStates()
        // Loop through the states and dynamically add buttons
        for (i in stateList.indices) {
            val stateName = stateList[i]
            val stateImage = stateImages[i]

            // Create a LinearLayout for each state (Button + Text)
            val stateLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 16, 8, 16) // Margins between buttons
                }
            }

            // Create the ImageButton
            val imageButton = ImageButton(this).apply {
                layoutParams =
                    LinearLayout.LayoutParams(70.dpToPx(), 70.dpToPx()) // Convert dp to pixels
                setBackgroundResource(R.drawable.circle_button_bg) // Circular background
                setImageResource(stateImage) // Set the state image
                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE // Ensure the image fits inside the button
                setPadding(
                    10.dpToPx(),
                    10.dpToPx(),
                    10.dpToPx(),
                    10.dpToPx()
                ) // Padding around the image
                contentDescription = stateName // Accessibility
                setOnClickListener {
                    // Handle state button click
                    stateLbl?.text = stateName
                    showDistrictsForState(stateName, cardContainer!!)
                }
            }

            // Create the TextView for state name
            val textView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = stateName
                textSize = 14f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
            }

            // Add ImageButton and TextView to the state layout
            stateLayout.addView(imageButton)
            stateLayout.addView(textView)

            // Add the state layout to the main LinearLayout inside ScrollView
            stateButtonsLayout?.addView(stateLayout)
        }
    }


    // Helper function to display districts for the selected state
    private fun showDistrictsForState(stateName: String, districtCardLayout: LinearLayout) {
        districtCardLayout.removeAllViews() // Clear previous district cards
        // Filter districts belonging to the selected state
        val state = allStatesWithRates.find { it.name == stateName }
        val districtsForState = state?.districts ?: emptyList()

        val districts = DropDownManager.getDistrictsForState(stateName)
        for (district in districts) {
            val districtCard =
                LayoutInflater.from(this).inflate(R.layout.chicken_rates_card, null, false)

            val districtNameTextView = districtCard.findViewById<TextView>(R.id.cityNameLabel)
            districtNameTextView.text = "$district Chicken Rates"

            val liftingRateTextView = districtCard.findViewById<TextView>(R.id.value1Top)
            val retailRateTextView = districtCard.findViewById<TextView>(R.id.value2Top)
            val skinRateTextView = districtCard.findViewById<TextView>(R.id.value3Top)
            val skinLessRateTextView = districtCard.findViewById<TextView>(R.id.value4Top)
            liftingRateTextView.text = "200"

            districtCardLayout.addView(districtCard)
        }
    }

    fun fetchDistrictRates() {
        loader.show()
        ApiHelper.get(
            url = "https://api.yourserver.com/Rates",
            responseType = RatesResponse::class.java,
            onSuccess = { response ->
                // Response successfully parsed into RatesResponse
                allDistrictRates.clear()
                allDistrictRates.addAll(response.districtRates)

                allStates.clear()
                allStates.addAll(response.states)

                loader.hide()
                println("DistrictRates: Rates fetched and stored successfully.")
            },
            onFailure = { error ->
                loader.hide()
                CustomAlertDialog(this)
                    .setTitle("Failed to load Data")
                    .setDescription("Go back and come to rates")
                    .showOkButton(true, "OK") {
                        println("Rates")
                    }
                    .showCancelButton(false)
                    .show()
                println("API Error : Failed to retrieve rates: $error")
            }
        )
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeRates::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}