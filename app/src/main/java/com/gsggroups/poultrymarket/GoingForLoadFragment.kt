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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.gsggroups.poultrymarket.Common.ApiHelper
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.SubmitLoadRequest
import com.gsggroups.poultrymarket.SharedDataFiles.SharedViewModel
import com.gsggroups.poultrymarket.Utils.ApiService
import com.gsggroups.poultrymarket.Utils.hideKeyboardAfterIdle
import com.gsggroups.poultrymarket.base.ApiClient
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private var goingBool: Boolean = false
    private lateinit var loadAvilableText: TextView
    private lateinit var load_available_switch: Switch
    private lateinit var headingText: TextView
    private lateinit var henCountEditText: EditText
    private lateinit var henSizeSpinner: Spinner
    private lateinit var farmSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var activateAllButton: Button
    private lateinit var loader: LoaderUtils
    var roleId: Int = 936

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels() // 👈 important!

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
        activateAllButton = view.findViewById(R.id.completed_activate_all_button)

        load_available_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loadAvilableText.setTextColor(Color.parseColor("#006400"))
                loadAvilableText.setTypeface(null, Typeface.BOLD)
            } else {
                loadAvilableText.setTextColor(Color.RED)
                loadAvilableText.setTypeface(null, Typeface.NORMAL)
            }
        }
henCountEditText.hideKeyboardAfterIdle()
        val userId = SharedPreferencesManager.getUserId(requireContext())
        val propertyId = SharedPreferencesManager.getPropertyId(requireContext())
        var loadDescription = "Going for Load"

        roleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 936
        val districtIds = listOf(1)
        headingText.text = "Going for Load"

        val editTextSalary = view.findViewById<EditText>(R.id.text_hen_count)
        formatIndianCurrency(editTextSalary)
        activateAllButton.setOnClickListener {
            val henSizeFromShared = SharedPreferencesManager.getHenSizeSubmitGoing(requireContext())
            val henCountFromShared = SharedPreferencesManager.getHenCountSubmitGoing(requireContext())
            // Submit after all validations passed
            if (propertyId != null) {
                if (userId != null) {
                    if (henSizeFromShared != null && henCountFromShared != null) {
                        submitLoadRequest(
                            userId,
                            propertyId,
                            loadDescription,
                            henCountFromShared,
                            henSizeFromShared,
                            isActivate = false,
                            isFromSubmit = false
                        )
                    } else {
                        submitLoadRequest(
                            userId,
                            propertyId,
                            loadDescription,
                            "1",   // default henCount
                            "1",   // default henSize
                            isActivate = false,
                            isFromSubmit = false
                        )
                    }

                }
            }
        }

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
            SharedPreferencesManager.saveLastSubmitTimeGoing(
                requireContext(),
                DateTimeUtils.formattedDateTime
            )
            // Submit after all validations passed
            submitLoadRequest(
                userId,
                propertyId,
                loadDescription,
                henCount,
                selectedHenSize,
                true,
                true
            )

        }
     checkAndDisableButtonGoingLoad()

    }
    private fun checkAndDisableButtonGoingLoad() {
        val ctx = requireContext()
        var lastSubmit: String? = null
        var isSubmitted = false

        // Pick correct pref based on role
        when (roleId) {
            UserRoles.ID_FARMER -> {
                lastSubmit = SharedPreferencesManager.getLastSubmitTimeBatch(ctx)
                isSubmitted = sharedViewModel.batchReady.value ?: false
            }

            UserRoles.ID_TRADER, UserRoles.ID_SHOPKEEPER -> {
                goingBool = SharedPreferencesManager.getGoingBoolean(ctx)
                lastSubmit = SharedPreferencesManager.getLastSubmitTimeGoing(ctx)
                isSubmitted = sharedViewModel.goingForLoad.value ?: false
            }
        }

        if (!lastSubmit.isNullOrEmpty() && lastSubmit != "0" && lastSubmit != "null") {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val lastTime = LocalDateTime.parse(lastSubmit, formatter)
            val now = LocalDateTime.now()
            val duration = Duration.between(lastTime, now)
            val maxDuration = Duration.ofHours(24)

            if (duration < maxDuration && isSubmitted) {
                // Still inside cooldown
                val remaining = maxDuration.minus(duration)
                val hours = remaining.toHours()
                val minutes = remaining.toMinutes() % 60

                submitButton.isEnabled = false
                submitButton.text = "Submitted (Wait ${hours} hrs ${minutes} mins)"
                submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                activateAllButton.isEnabled = true
                activateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
                return
            } else {
                when (roleId) {
                    UserRoles.ID_FARMER -> SharedPreferencesManager.clearLastSubmitTimeBatch(ctx)
                    UserRoles.ID_TRADER, UserRoles.ID_SHOPKEEPER -> SharedPreferencesManager.clearLastSubmitTimeGoing(
                        ctx
                    )
                }
            }
        }
            if (goingBool) {
                submitButton.isEnabled = false
                submitButton.text = "Submit"
                submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                activateAllButton.isEnabled = true
                activateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
            }else{
                submitButton.isEnabled = true
                submitButton.text = "Submit"
                submitButton.setBackgroundColor(Color.parseColor("#FF6347"))

                activateAllButton.isEnabled = false
                activateAllButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
            }
    }


/*
    private fun checkAndDisableButtonIfNeeded() {
        val ctx = requireContext()

        // Pick correct SharedPref key based on role
        val lastSubmit = when (roleId) {
            UserRoles.ID_FARMER -> SharedPreferencesManager.getLastSubmitTimeBatch(ctx)
            UserRoles.ID_TRADER -> SharedPreferencesManager.getLastSubmitTimeGoing(ctx)
            else -> SharedPreferencesManager.getLastSubmitTimeGoing(ctx)
        }

        if (!lastSubmit.isNullOrEmpty() && lastSubmit != "0" && lastSubmit != "null") {
            val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val lastTime = java.time.LocalDateTime.parse(lastSubmit, formatter)
            val now = java.time.LocalDateTime.now()

            val duration = java.time.Duration.between(lastTime, now)
            val maxDuration = java.time.Duration.ofHours(24)

            if (duration < maxDuration) {
                val remaining = maxDuration.minus(duration)
                val hours = remaining.toHours()
                val minutes = remaining.toMinutes() % 60

                submitButton.isEnabled = false
                submitButton.text = "Submitted (Wait ${hours} hrs ${minutes} mins)"
                submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                sharedViewModel.setGoingForLoad(true)

            } else {
                submitButton.isEnabled = true
                submitButton.text = "Submit"
                submitButton.setBackgroundColor(Color.parseColor("#FF6347"))

                // clear the correct pref when expired
                when (roleId) {
                    UserRoles.ID_TRADER -> SharedPreferencesManager.clearLastSubmitTimeGoing(ctx)
                    else -> SharedPreferencesManager.clearLastSubmitTimeGoing(ctx)
                }
            }
        } else {
            // No saved submit → normal state
            submitButton.isEnabled = true
            submitButton.text = "Submit"
            submitButton.setBackgroundColor(Color.parseColor("#FF6347"))
            activateAllButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
        }
    }
*/

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
        propertyId: String,
        loadDescription: String,
        henCount: String,
        henSize: String,
        isActivate: Boolean,
        isFromSubmit: Boolean
    ) {
  //      loader.show()
        val henCount = henCount
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
        val selectedHenSize = henSize?.toString() ?: "0 kg"
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
            goingForLoad = isActivate
        )

        val call = ApiClient.retrofit
                .create(ApiService::class.java)
                .goingForLoad(request)
        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                val ctx = activity?.applicationContext ?: return@post

                if (response.isSuccess) {
                    ctx?.let {
                        Toast.makeText(it, response.message, Toast.LENGTH_LONG).show()
                    }
                    if (isFromSubmit) {
                        // Submit clicked
                        submitButton.isEnabled = false
                        submitButton.text = "Submitted (Wait 24 hrs)"
                        submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                        activateAllButton.isEnabled = true
                        activateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))

                        when (roleId) {
                            UserRoles.ID_FARMER -> SharedPreferencesManager.saveLastSubmitTimeBatch(ctx, DateTimeUtils.formattedDateTime)
                            UserRoles.ID_TRADER -> SharedPreferencesManager.saveLastSubmitTimeGoing(ctx, DateTimeUtils.formattedDateTime)
                            else -> SharedPreferencesManager.saveLastSubmitTimeGoing(ctx, DateTimeUtils.formattedDateTime)
                        }
                        sharedViewModel.setGoingForLoad(true)
                        SharedPreferencesManager.saveGoingBoolean(ctx,true)

                    } else {
                        // ActivateAll clicked
                        activateAllButton.isEnabled = false
                        activateAllButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                        submitButton.isEnabled = true
                        submitButton.text = "Submit"
                        submitButton.setBackgroundColor(Color.parseColor("#FF6347"))

                        SharedPreferencesManager.clearLastSubmitTimeGoing(ctx)
                        SharedPreferencesManager.saveGoingBoolean(ctx,false)

                        sharedViewModel.setGoingForLoad(false)
                    }
                    // reset input fields
                    henCountEditText.text.clear()
                    henSizeSpinner.setSelection(0)
                } else {
                    Log.e("SubmitLoad", "Failed: ${response.message}")
                    Toast.makeText(
                        ctx,
                        "Submit failed: ${response.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
               // loader.hide()
            },
            onFailure = { error ->
                Log.e("SubmitLoad", "Error: $error")
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
            //    loader.hide()
            }
        )
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
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

    override fun onPause() {
        super.onPause()
        if (SharedPreferencesManager.getGoingBoolean(requireContext()) == false) {
            (requireActivity() as? Dashboard)?.updateGoingForLoadSwitch(null, false)
//            sharedViewModel.setGoingForLoad(false)
        }
    }
}