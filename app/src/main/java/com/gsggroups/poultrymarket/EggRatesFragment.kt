package com.gsggroups.poultrymarket

import android.content.Context
import android.graphics.Color
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
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.CustomAlertDialog
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
 * Use the [EggRatesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EggRatesFragment : Fragment() {

    private lateinit var stateLbl: TextView
    private lateinit var loader: LoaderUtils
    private val allDistrictRates = mutableListOf<DistrictRates>()
    private var allStatesWithRates: List<State> = listOf()
    val allStates: MutableList<State> = mutableListOf()

    private val stateList = listOf(
        "Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
        "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand",
        "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra",
        "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh",
        "Uttarakhand", "West Bengal"
    )
    private val stateImages = listOf(
        R.drawable.egg_rate_side, R.drawable.egg_rate_side, R.drawable.egg_rate_side, R.drawable.egg_rate_side, R.drawable.hen3,
        R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.egg_rate_side,
        R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.egg_rate_side, R.drawable.egg_rate_side, R.drawable.hen3,
        R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3, R.drawable.hen3,
        R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.hen3, R.drawable.egg_rate_side, R.drawable.hen3,
        R.drawable.egg_rate_side, R.drawable.egg_rate_side, R.drawable.hen3, R.drawable.hen3, R.drawable.egg_rate_side,
        R.drawable.egg_rate_side
    )

    private val districtsMap = mapOf(
        "Andaman and Nicobar Islands" to listOf("Nicobar", "North and Middle Andaman", "South Andaman"),
        "Andhra Pradesh" to listOf("Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna", "Kurnool", "Nellore", "Prakasam", "Srikakulam", "Visakhapatnam", "Vizianagaram", "West Godavari", "YSR Kadapa"),
        "Arunachal Pradesh" to listOf("Tawang", "West Kameng", "East Kameng", "Papum Pare", "Kurung Kumey", "Kra Daadi", "Lower Subansiri", "Upper Subansiri", "West Siang", "East Siang", "Siang", "Upper Siang", "Lower Siang", "Lower Dibang Valley", "Dibang Valley", "Anjaw", "Lohit", "Namsai", "Changlang", "Tirap", "Longding"),
        "Assam" to listOf("Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo", "Chirang", "Darrang", "Dhemaji", "Dhubri", "Dibrugarh", "Goalpara", "Golaghat", "Hailakandi", "Hojai", "Jorhat", "Kamrup", "Kamrup Metropolitan", "Karbi Anglong", "Karimganj", "Kokrajhar", "Lakhimpur", "Majuli", "Morigaon", "Nagaon", "Nalbari", "Dima Hasao", "Sivasagar", "Sonitpur", "South Salmara-Mankachar", "Tinsukia", "Udalguri", "West Karbi Anglong"),
        "Bihar" to listOf("Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur", "Buxar", "Darbhanga", "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad", "Kaimur", "Katihar", "Khagaria", "Kishanganj", "Lakhisarai", "Madhepura", "Madhubani", "Munger", "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas", "Saharsa", "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi", "Siwan", "Supaul", "Vaishali", "West Champaran"),
        "Chandigarh" to listOf("Chandigarh"),
        "Chhattisgarh" to listOf("Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur", "Dantewada", "Dhamtari", "Durg", "Gariaband", "Janjgir-Champa", "Jashpur", "Kabirdham", "Kanker", "Kondagaon", "Korba", "Koriya", "Mahasamund", "Mungeli", "Narayanpur", "Raigarh", "Raipur", "Rajnandgaon", "Sukma", "Surajpur", "Surguja"),
        "Dadra and Nagar Haveli and Daman and Diu" to listOf("Dadra and Nagar Haveli", "Daman", "Diu"),
        "Delhi" to listOf("Central Delhi", "East Delhi", "New Delhi", "North Delhi", "North East Delhi", "North West Delhi", "Shahdara", "South Delhi", "South East Delhi", "South West Delhi", "West Delhi"),
        "Goa" to listOf("North Goa", "South Goa"),
        "Gujarat" to listOf("Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar", "Botad", "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar", "Gir Somnath", "Jamnagar", "Junagadh", "Kheda", "Kutch", "Mahisagar", "Mehsana", "Morbi", "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha", "Surat", "Surendranagar", "Tapi", "Vadodara", "Valsad"),
        "Haryana" to listOf("Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurgaon", "Hisar", "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Mewat", "Palwal", "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"),
        "Himachal Pradesh" to listOf("Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", "Lahaul and Spiti", "Mandi", "Shimla", "Sirmaur", "Solan", "Una"),
        "Jammu and Kashmir" to listOf("Anantnag", "Bandipora", "Baramulla", "Budgam", "Doda", "Ganderbal", "Jammu", "Kathua", "Kishtwar", "Kulgam", "Kupwara", "Poonch", "Pulwama", "Rajouri", "Ramban", "Reasi", "Samba", "Shopian", "Srinagar", "Udhampur"),
        "Jharkhand" to listOf("Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum", "Garhwa", "Giridih", "Godda", "Gumla", "Hazaribagh", "Jamtara", "Khunti", "Koderma", "Latehar", "Lohardaga", "Pakur", "Palamu", "Ramgarh", "Ranchi", "Sahebganj", "Seraikela Kharsawan", "Simdega", "West Singhbhum"),
        "Karnataka" to listOf("Bagalkot", "Bangalore Rural", "Bangalore Urban", "Belgaum", "Bellary", "Bidar", "Chamarajanagar", "Chikballapur", "Chikkamagaluru", "Chitradurga", "Dakshina Kannada", "Davanagere", "Dharwad", "Gadag", "Gulbarga", "Hassan", "Haveri", "Kodagu", "Kolar", "Koppal", "Mandya", "Mysore", "Raichur", "Ramanagara", "Shimoga", "Tumkur", "Udupi", "Uttara Kannada", "Vijayapura", "Yadgir"),
        "Kerala" to listOf("Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam", "Kottayam", "Kozhikode", "Malappuram", "Palakkad", "Pathanamthitta", "Thiruvananthapuram", "Thrissur", "Wayanad"),
        "Ladakh" to listOf("Kargil", "Leh"),
        "Lakshadweep" to listOf("Agatti", "Amini", "Androth", "Bithra", "Chethlath", "Kavaratti", "Kadmat", "Kalpeni", "Kilthan", "Minicoy"),
        "Madhya Pradesh" to listOf("Agar Malwa", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani", "Betul", "Bhind", "Bhopal", "Burhanpur", "Chhatarpur", "Chhindwara", "Damoh", "Datia", "Dewas", "Dhar", "Dindori", "Guna", "Gwalior", "Harda", "Hoshangabad", "Indore", "Jabalpur", "Jhabua", "Katni", "Khandwa", "Khargone", "Mandla", "Mandsaur", "Morena", "Narsinghpur", "Neemuch", "Panna", "Raisen", "Rajgarh", "Ratlam", "Rewa", "Sagar", "Satna", "Sehore", "Seoni", "Shahdol", "Shajapur", "Sheopur", "Shivpuri", "Sidhi", "Singrauli", "Tikamgarh", "Ujjain", "Umaria", "Vidisha"),
        "Maharashtra" to listOf("Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed", "Bhandara", "Buldhana", "Chandrapur", "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna", "Kolhapur", "Latur", "Mumbai", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar", "Nashik", "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri", "Sangli", "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"),
        "Manipur" to listOf("Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West", "Jiribam", "Kakching", "Kamjong", "Kangpokpi", "Noney", "Pherzawl", "Senapati", "Tamenglong", "Tengnoupal", "Thoubal", "Ukhrul"),
        "Meghalaya" to listOf("East Garo Hills", "East Jaintia Hills", "East Khasi Hills", "North Garo Hills", "Ri Bhoi", "South Garo Hills", "South West Garo Hills", "South West Khasi Hills", "West Garo Hills", "West Jaintia Hills", "West Khasi Hills"),
        "Mizoram" to listOf("Aizawl", "Champhai", "Kolasib", "Lawngtlai", "Lunglei", "Mamit", "Saiha", "Serchhip"),
        "Nagaland" to listOf("Dimapur", "Kiphire", "Kohima", "Longleng", "Mokokchung", "Mon", "Peren", "Phek", "Tuensang", "Wokha", "Zunheboto"),
        "Odisha" to listOf("Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack", "Debagarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghpur", "Jajpur", "Jharsuguda", "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar", "Khordha", "Koraput", "Malkangiri", "Mayurbhanj", "Nabarangpur", "Nayagarh", "Nuapada", "Puri", "Rayagada", "Sambalpur", "Sonepur", "Sundergarh"),
        "Puducherry" to listOf("Karaikal", "Mahe", "Puducherry", "Yanam"),
        "Punjab" to listOf("Amritsar", "Barnala", "Bathinda", "Faridkot", "Fatehgarh Sahib", "Fazilka", "Ferozepur", "Gurdaspur", "Hoshiarpur", "Jalandhar", "Kapurthala", "Ludhiana", "Mansa", "Moga", "Muktsar", "Nawanshahr", "Pathankot", "Patiala", "Rupnagar", "Sangrur", "SAS Nagar", "Sri Muktsar Sahib", "Tarn Taran"),
        "Rajasthan" to listOf("Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara", "Bikaner", "Bundi", "Chittorgarh", "Churu", "Dausa", "Dholpur", "Dungarpur", "Hanumangarh", "Jaipur", "Jaisalmer", "Jalore", "Jhalawar", "Jhunjhunu", "Jodhpur", "Karauli", "Kota", "Nagaur", "Pali", "Pratapgarh", "Rajsamand", "Sawai Madhopur", "Sikar", "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"),
        "Sikkim" to listOf("East Sikkim", "North Sikkim", "South Sikkim", "West Sikkim"),
        "Tamil Nadu" to listOf("Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kallakurichi", "Kancheepuram", "Karur", "Krishnagiri", "Madurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur", "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivaganga", "Tenkasi", "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli", "Tirunelveli", "Tirupathur", "Tiruppur", "Tiruvallur", "Tiruvannamalai", "Tiruvarur", "Vellore", "Viluppuram", "Virudhunagar"),
        "Telangana" to listOf("Adilabad", "Bhadradri Kothagudem", "Hyderabad", "Jagtial", "Jangaon", "Jayashankar Bhupalpally", "Jogulamba Gadwal", "Kamareddy", "Karimnagar", "Khammam", "Kumuram Bheem Asifabad", "Mahabubabad", "Mahabubnagar", "Mancherial", "Medak", "Medchal–Malkajgiri", "Mulugu", "Nagarkurnool", "Nalgonda", "Narayanpet", "Nirmal", "Nizamabad", "Peddapalli", "Rajanna Sircilla", "Ranga Reddy", "Sangareddy", "Siddipet", "Suryapet", "Vikarabad", "Wanaparthy", "Warangal Rural", "Warangal Urban", "Yadadri Bhuvanagiri"),
        "Tripura" to listOf("Dhalai", "Gomati", "Khowai", "North Tripura", "Sepahijala", "South Tripura", "Unakoti", "West Tripura"),
        "Uttar Pradesh" to listOf("Agra", "Aligarh", "Ambedkar Nagar", "Amethi", "Amroha", "Auraiya", "Ayodhya", "Azamgarh", "Badaun", "Baghpat", "Bahraich", "Ballia", "Balrampur", "Banda", "Barabanki", "Bareilly", "Basti", "Bijnor", "Bulandshahr", "Chandauli", "Chitrakoot", "Deoria", "Etah", "Etawah", "Farrukhabad", "Fatehpur", "Firozabad", "Gautam Buddha Nagar", "Ghaziabad", "Ghazipur", "Gonda", "Gorakhpur", "Hamirpur", "Hapur", "Hardoi", "Hathras", "Jalaun", "Jaunpur", "Jhansi", "Kannauj", "Kanpur Dehat", "Kanpur Nagar", "Kasganj", "Kaushambi", "Kheri", "Kushinagar", "Lalitpur", "Lucknow", "Maharajganj", "Mahoba", "Mainpuri", "Mathura", "Mau", "Meerut", "Mirzapur", "Moradabad", "Muzaffarnagar", "Pilibhit", "Pratapgarh", "Prayagraj", "Raebareli", "Rampur", "Saharanpur", "Sambhal", "Sant Kabir Nagar", "Sant Ravidas Nagar", "Shahjahanpur", "Shamli", "Shrawasti", "Siddharthnagar", "Sitapur", "Sonbhadra", "Sultanpur", "Unnao", "Varanasi"),
        "Uttarakhand" to listOf("Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar", "Nainital", "Pauri Garhwal", "Pithoragarh", "Rudraprayag", "Tehri Garhwal", "Udham Singh Nagar", "Uttarkashi"),
        "West Bengal" to listOf("Alipurduar", "Bankura", "Birbhum", "Cooch Behar", "Dakshin Dinajpur", "Darjeeling", "Hooghly", "Howrah", "Jalpaiguri", "Jhargram", "Kalimpong", "Kolkata", "Malda", "Murshidabad", "Nadia", "North 24 Parganas", "Paschim Bardhaman", "Paschim Medinipur", "Purba Bardhaman", "Purba Medinipur", "Purulia", "South 24 Parganas", "Uttar Dinajpur")
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
        loader = LoaderUtils(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_egg_rates, container, false)

        // Find the LinearLayout inside ScrollView where we will add the buttons
        val stateButtonsLayoutEgg = view.findViewById<LinearLayout>(R.id.stateButtonLayoutEgg)
        val cardContainer = view.findViewById<LinearLayout>(R.id.eggCardWidgetLayout)
        stateLbl = view.findViewById<TextView>(R.id.stateNameLabel)
        fetchDistrictRates()

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
                    stateLbl.text = stateName
                    showDistrictsForStateEgg(stateName, cardContainer)
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
            stateButtonsLayoutEgg.addView(stateLayout)
        }
        return view
    }

    // Helper function to display districts for the selected state
    private fun showDistrictsForStateEgg(stateName: String, districtCardLayout: LinearLayout) {
        districtCardLayout.removeAllViews() // Clear previous district cards
        // Filter districts belonging to the selected state
        val state = allStatesWithRates.find { it.name == stateName }
        val districtsForState = state?.districts ?: emptyList()

        val districts = districtsMap[stateName] ?: listOf()
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
         * @return A new instance of fragment EggRatesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EggRatesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}