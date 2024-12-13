package com.gsggroups.poultrymarket

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Model.DistrictRates
import com.gsggroups.poultrymarket.Model.RatesResponse
import com.gsggroups.poultrymarket.Model.State


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChickenRatesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChickenRatesFragment : Fragment() {
    private lateinit var stateLbl: TextView
    private val allDistrictRates = mutableListOf<DistrictRates>()
    private var allStatesWithRates: List<State> = listOf()
    private val allStates: MutableList<State> = mutableListOf()

    private lateinit var loader: LoaderUtils

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

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Initialize the LoaderUtils with the context
        loader = LoaderUtils(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chicken_rates, container, false)
        // Find the LinearLayout inside ScrollView where we will add the buttons
        val stateButtonsLayout = view.findViewById<LinearLayout>(R.id.stateButtonLayout)
        val cardContainer = view.findViewById<LinearLayout>(R.id.chickenCardContainerLayout)
        stateLbl = view.findViewById<TextView>(R.id.stateNameLabel)
//        fetchDistrictRates()

        val stateList = DropDownManager.getStates()
        // Loop through the states and dynamically add buttons
        for (i in stateList.indices) {
            val stateName = stateList[i]
            val stateImage = stateImages[i]

            // Create a LinearLayout for each state (Button + Text)
            val stateLayout = LinearLayout(requireContext()).apply {
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
            val imageButton = ImageButton(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(70.dpToPx(), 70.dpToPx()) // Convert dp to pixels
                setBackgroundResource(R.drawable.circle_button_bg) // Circular background
                setImageResource(stateImage) // Set the state image
                scaleType = ImageView.ScaleType.CENTER_INSIDE // Ensure the image fits inside the button
                setPadding(10.dpToPx(), 10.dpToPx(), 10.dpToPx(), 10.dpToPx()) // Padding around the image
                contentDescription = stateName // Accessibility
                setOnClickListener {
                    // Handle state button click
                    stateLbl.text = stateName
                    showDistrictsForState(stateName, cardContainer)
                }
            }

            // Create the TextView for state name
            val textView = TextView(requireContext()).apply {
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
            stateButtonsLayout.addView(stateLayout)
        }

        return view
    }

    // Helper function to display districts for the selected state
    private fun showDistrictsForState(stateName: String, districtCardLayout: LinearLayout) {
        districtCardLayout.removeAllViews() // Clear previous district cards
        // Filter districts belonging to the selected state
        val state = allStatesWithRates.find { it.name == stateName }
        val districtsForState = state?.districts ?: emptyList()

        val districts = DropDownManager.getDistrictsForState(stateName)
        for (district in districts) {
            val districtCard = LayoutInflater.from(requireContext()).inflate(R.layout.chicken_rates_card, null, false)

            val districtNameTextView = districtCard.findViewById<TextView>(R.id.cityNameLabel)
            districtNameTextView.text = "$district Chicken Rates"

            val liftingRateTextView = districtCard.findViewById<TextView>(R.id.value1Top)
            val retailRateTextView = districtCard.findViewById<TextView>(R.id.value2Top)
            val skinRateTextView = districtCard.findViewById<TextView>(R.id.value3Top)
            val skinLessRateTextView = districtCard.findViewById<TextView>(R.id.value4Top)
//            val eggRateTextView = districtCard.findViewById<TextView>(R.id.eggRateLabel)

//            liftingRateTextView.text = "${district.liftingRate}"
//            retailRateTextView.text = "Retail Rate: ${district.retailRate}"
//            skinRateTextView.text = "Skin Rate: ${district.skinRate}"
//            skinLessRateTextView.text = "Skinless Rate: ${district.skinLessRate}"

//            districtCard.setOnClickListener {
//                Toast.makeText(requireContext(), "$district selected!", Toast.LENGTH_SHORT).show()
//            }

                                /*
                                selectedState?.districts?.forEach { districtRates ->
                            val districtCard = LayoutInflater.from(requireContext()).inflate(R.layout.chicken_rates_card, null, false)

                            // Set district name and rates on the card
                            val districtNameTextView = districtCard.findViewById<TextView>(R.id.cityNameLabel)
                            districtNameTextView.text = "${districtRates.stateName} Chicken Rates"

                            val liftingRateTextView = districtCard.findViewById<TextView>(R.id.liftingRateLabel)
                            liftingRateTextView.text = "Lifting Rate: ${districtRates.liftingRate}"

                            val retailRateTextView = districtCard.findViewById<TextView>(R.id.retailRateLabel)
                            retailRateTextView.text = "Retail Rate: ${districtRates.retailRate}"

                            val skinRateTextView = districtCard.findViewById<TextView>(R.id.skinRateLabel)
                            skinRateTextView.text = "Skin Rate: ${districtRates.skinRate}"

                            val skinLessRateTextView = districtCard.findViewById<TextView>(R.id.skinLessRateLabel)
                            skinLessRateTextView.text = "Skinless Rate: ${districtRates.skinLessRate}"

                            val eggRateTextView = districtCard.findViewById<TextView>(R.id.eggRateLabel)
                            eggRateTextView.text = "Egg Rate: ${districtRates.eggRate}"

                            // Add the district card to the layout
                            districtCardLayout.addView(districtCard)
                        }
                                 */


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
                CustomAlertDialog(requireContext())
                    .setTitle("Failed to load Data")
                    .setDescription("Go back and come to rates")
                    .showOkButton(true, "OK") {
                        println("Retrying rates fetch.")
                    }
                    .showCancelButton(false)
                    .show()
                println("API Error: Failed to retrieve rates: $error")
            }
        )

    }


    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChickenRatesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChickenRatesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}