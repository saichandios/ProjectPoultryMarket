package com.gsggroups.poultrymarket

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.Model.SubmitLoadRequest
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.base.ApiClient
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GoingForLoadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoingForLoadFragment : Fragment() {
    private lateinit var loadAvilableText: TextView
    private lateinit var load_available_switch: Switch
    private lateinit var headingText: TextView
    private lateinit var henCountEditText: EditText
    private lateinit var henSizeSpinner: Spinner
    private lateinit var farmSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var loader: LoaderUtils
    var roleId: Int = 936

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_going_for_load, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loader = LoaderUtils(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAvilableText = view.findViewById(R.id.load_available_text)
        load_available_switch = view.findViewById(R.id.load_available_switch)
        headingText = view.findViewById(R.id.going_for_load_text)
        henCountEditText = view.findViewById(R.id.text_hen_count)
        henSizeSpinner = view.findViewById(R.id.hen_size_spinner) // use ID if set
        farmSpinner = view.findViewById(R.id.farm_spinner)         // use ID if set
        submitButton = view.findViewById(R.id.submit_button)

        // Set up a listener to change text color based on switch state
        load_available_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loadAvilableText.setTextColor(Color.parseColor("#006400"))
                loadAvilableText.setTypeface(null, Typeface.BOLD)
            } else {
                loadAvilableText.setTextColor(Color.RED)
                loadAvilableText.setTypeface(null, Typeface.NORMAL)
            }
        }

        val userId = SharedPreferencesManager.getUserId(requireContext())
        val propertyId = SharedPreferencesManager.getPropertyId(requireContext())
        roleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 936
        val districtIds = listOf(1)
        headingText.text = "Going for Load"
        val editTextSalary = view.findViewById<EditText>(R.id.text_hen_count)
        formatIndianCurrency(editTextSalary)

        submitButton.setOnClickListener {
            val henCount = henCountEditText.text.toString().trim()
            val selectedHenSize = henSizeSpinner.selectedItem?.toString()

            if (henCount.isEmpty()) {
                henCountEditText.error = "Please enter hen count"
                henCountEditText.requestFocus()
                return@setOnClickListener
            }

            // Validate Hen Size
            if (selectedHenSize.isNullOrEmpty() || selectedHenSize == "Select Size") {
                Toast.makeText(requireContext(), "Please select a hen size", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate User ID and Property ID
            if (userId == null || propertyId == null) {
                Toast.makeText(requireContext(), "User or Property ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Submit after all validations passed
            submitLoadRequest(userId, propertyId)
        }

        checkAndDisableButtonIfNeeded()
    }

    private fun checkAndDisableButtonIfNeeded() {
        var lastSubmit = SharedPreferencesManager.getLastSubmitTimeGoing(requireContext()).toString()
        if (lastSubmit != null && lastSubmit != "0") {
            val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val lastTime = java.time.LocalDateTime.parse(lastSubmit, formatter)
            val now = java.time.LocalDateTime.now()

            val duration = java.time.Duration.between(lastTime, now)
            if (duration.toHours() < 24) {
                submitButton.isEnabled = true
                submitButton.text = "Submitted (Wait 24 hrs)"
                submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
            } else {
                submitButton.isEnabled = true
                submitButton.text = "Submit"
                submitButton.setBackgroundColor(Color.parseColor("#FF6347"))
                SharedPreferencesManager.clearLastSubmitTimeGoing(requireContext())
            }
        } else {
            submitButton.isEnabled = true
            submitButton.text = "Submit"
            submitButton.setBackgroundColor(Color.parseColor("#FF6347"))
        }

    }

    fun formatIndianCurrency(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var currentText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No operation needed before text change
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No operation needed on text change
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentText) {
                    editText.removeTextChangedListener(this)

                    // Remove any existing commas from the text
                    val cleanString = s.toString().replace("[,]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        try {
                            // Parse the cleaned string to a long number
                            val parsed = cleanString.toLong()

                            // Format the number using Indian currency format
                            val formatter = DecimalFormat("#,##,###", DecimalFormatSymbols(Locale("en", "IN")))
                            val formatted = formatter.format(parsed)

                            // Update the text with formatted value and set the cursor position
                            currentText = formatted
                            editText.setText(formatted)
                            editText.setSelection(formatted.length) // Move cursor to the end
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun submitLoadRequest(
        userId: String,
        propertyId: String
    ) {
        loader.show()
        val henCount = henCountEditText.text.toString()
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
        val selectedHenSize = henSizeSpinner.selectedItem?.toString() ?: "0 kg"
        val henSizeFloat = selectedHenSize
            .replace("kg", "", ignoreCase = true)
            .trim()
            .toFloatOrNull() ?: 0f

        val request = SubmitLoadRequest(
            userId = userId,
            propertyId = propertyId,
            distrcitIds = listOf(1, 2),
            message = "Going For Load",
            roleId = roleId,
            henCount = henCount,
            henWeight = henSizeFloat,
            loadAvailable = true
        )

        val call = ApiClient.retrofit
                .create(ApiService::class.java)
                .goingForLoad(request)
        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                if (response.isSuccess) {
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    submitButton.isEnabled = false
                    submitButton.text = "Submitted (Wait 24 hrs)"
                    submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
                    henCountEditText.text.clear()
                    henSizeSpinner.setSelection(0)
                    SharedPreferencesManager.saveLastSubmitTimeGoing(
                            requireContext(),
                            DateTimeUtils.formattedDateTime)
                } else {
                    Log.e("SubmitLoad", "Failed: ${response.message}")
                    Toast.makeText(context, "Submit failed: ${response.message}", Toast.LENGTH_SHORT).show()
                }
                loader.hide()
            },
            onFailure = { error ->
                Log.e("SubmitLoad", "Error: $error")
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                loader.hide()
            }
        )
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GoingForLoadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GoingForLoadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}