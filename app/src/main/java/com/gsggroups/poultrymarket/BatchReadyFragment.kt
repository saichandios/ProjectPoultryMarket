package com.gsggroups.poultrymarket

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
 * Use the [BatchReadyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
object DateTimeUtils {
    val formattedDateTime: String =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

class BatchReadyFragment : Fragment() {

    private var batchBool: Boolean = false
    private var needLoadBool: Boolean = false
    private lateinit var loadAvilableText: TextView
    private lateinit var load_available_switch: Switch
    private lateinit var headingText: TextView
    private lateinit var henCountEditText: EditText
    private lateinit var henSizeSpinner: Spinner
    private lateinit var farmSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var ActivateAllButton: Button
    private lateinit var loader: LoaderUtils
    var roleId: Int = 936
    private var debounceHandler = android.os.Handler()
    private var hideKeyboardRunnable: Runnable? = null

    private val sharedViewModel: SharedViewModel by activityViewModels() // 👈 important!

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
        return inflater.inflate(R.layout.fragment_batch_ready, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loader = LoaderUtils(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAvilableText = view.findViewById(R.id.load_available_text)
        load_available_switch = view.findViewById(R.id.load_available_switch)
        headingText = view.findViewById(R.id.Batch_Ready_text)
        henCountEditText = view.findViewById(R.id.text_hen_count)
        henSizeSpinner = view.findViewById(R.id.hen_size_spinner) // use ID if set
        farmSpinner = view.findViewById(R.id.farm_spinner)         // use ID if set
        submitButton = view.findViewById(R.id.submit_button_batch_need)
        ActivateAllButton = view.findViewById(R.id.completed_activate_all_button_batch)
        //   sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        /*  // Set up a listener to change text color based on switch state
          load_available_switch.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  loadAvilableText.setTextColor(Color.parseColor("#006400"))
                  loadAvilableText.setTypeface(null, Typeface.BOLD)
              } else {
                  loadAvilableText.setTextColor(Color.RED)
                  loadAvilableText.setTypeface(null, Typeface.NORMAL)
              }
          }
        */
        val userId = SharedPreferencesManager.getUserId(requireContext())
        val propertyId = SharedPreferencesManager.getPropertyId(requireContext())
        roleId = SharedPreferencesManager.getRoleId(requireContext())?.toInt() ?: 936
        val districtIds = listOf(1)
        var loadDescription = ""
        if (roleId == UserRoles.ID_FARMER) {
            loadDescription = "Batch Ready"

        } else {
            loadDescription = "Need Load"
        }
        checkAndDisableButtonIfNeeded()

        headingText.text = loadDescription
        val editTextSalary = view.findViewById<EditText>(R.id.text_hen_count)
        formatIndianCurrency(editTextSalary)

        henSizeSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                henSizeSpinner.hideKeyboard()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
        henCountEditText.hideKeyboardAfterIdle()



        ActivateAllButton.setOnClickListener {
            val henSizeFromShared = SharedPreferencesManager.getHenSizeSubmitNeed(requireContext())
            val henCountFromShared =
                SharedPreferencesManager.getHenCountSubmitNeed(requireContext())
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

                        //                  sharedViewModel.setBatchReady(false) // Turn switch ON
//                        sharedViewModel.setNeedLoad(false) // Turn switch ON
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
                        //        sharedViewModel.setBatchReady(false) // Turn switch ON
//                        sharedViewModel.setNeedLoad(false) // Turn switch ON
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
            if (selectedHenSize.isNullOrEmpty() || selectedHenSize == "Select Hen Size") {
                Toast.makeText(requireContext(), "Please select a hen size", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // Validate User ID and Property ID
            if (userId == null || propertyId == null) {
                Toast.makeText(requireContext(), "User or Property ID missing", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (roleId == UserRoles.ID_FARMER) {
                SharedPreferencesManager.saveLastSubmitTimeBatch(
                    requireContext(),
                    DateTimeUtils.formattedDateTime
                )
            } else {
                SharedPreferencesManager.saveLastSubmitTimeNeed(
                    requireContext(),
                    DateTimeUtils.formattedDateTime
                )
            }

            // Submit after all validations passed
            submitLoadRequest(
                userId,
                propertyId,
                loadDescription,
                henCount,
                selectedHenSize,
                isActivate = true,
                isFromSubmit = true
            )

            //           sharedViewModel.setBatchReady(true)
//            sharedViewModel.setNeedLoad(true)
        }

        checkAndDisableButtonIfNeeded()
    }

    private fun checkAndDisableButtonIfNeeded() {
        val ctx = requireContext()
        var lastSubmit: String? = null
        var isSubmitted = false

        // Pick correct pref based on role
        when (roleId) {
            UserRoles.ID_FARMER -> {
                batchBool = SharedPreferencesManager.getBatchBoolean(ctx)
                lastSubmit = SharedPreferencesManager.getLastSubmitTimeBatch(ctx)
                isSubmitted = sharedViewModel.batchReady.value ?: false
            }

            UserRoles.ID_TRADER, UserRoles.ID_SHOPKEEPER -> {
                needLoadBool = SharedPreferencesManager.getNeedBoolean(ctx)

                lastSubmit = SharedPreferencesManager.getLastSubmitTimeNeed(ctx)
                isSubmitted = sharedViewModel.needLoad.value ?: false
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

                ActivateAllButton.isEnabled = true
                ActivateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
                return
            } else {
                when (roleId) {
                    UserRoles.ID_FARMER -> SharedPreferencesManager.clearLastSubmitTimeBatch(ctx)
                    UserRoles.ID_TRADER, UserRoles.ID_SHOPKEEPER -> SharedPreferencesManager.clearLastSubmitTimeNeed(
                        ctx
                    )
                }
            }
        }
        if (batchBool && roleId == UserRoles.ID_FARMER) {
            submitButton.isEnabled = false
            submitButton.text = "Submit"
            submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

            ActivateAllButton.isEnabled = true
            ActivateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
        } else
            if (needLoadBool && roleId == UserRoles.ID_TRADER) {
                submitButton.isEnabled = false
                submitButton.text = "Submit"
                submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                ActivateAllButton.isEnabled = true
                ActivateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
            } else
                if (needLoadBool && roleId == UserRoles.ID_SHOPKEEPER) {
                    submitButton.isEnabled = false
                    submitButton.text = "Submit"
                    submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                    ActivateAllButton.isEnabled = true
                    ActivateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
                } else {
                    submitButton.isEnabled = true
                    submitButton.text = "Submit"
                    submitButton.setBackgroundColor(Color.parseColor("#FF6347"))

                    ActivateAllButton.isEnabled = false
                    ActivateAllButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
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
                            val formatter =
                                DecimalFormat("#,##,###", DecimalFormatSymbols(Locale("en", "IN")))
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BatchReadyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BatchReadyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun submitLoadRequest(
        userId: String,
        propertyId: String,
        message: String,
        henCount: String,
        selectedHenSize: String,
        isActivate: Boolean,
        isFromSubmit: Boolean

    ) {
        //   loader.show()
        Log.d("SubmitLoad", "Preparing to submit load request $isFromSubmit")

        val cleanHenCount = henCount.trim().replace(",", "")
        val safeHenSize = if (selectedHenSize.isEmpty()) "0 kg" else selectedHenSize

        val henSizeFloat = safeHenSize
            .replace("kg", "", ignoreCase = true)
            .trim()
            .toFloatOrNull() ?: 0f

        val request = when (roleId) {
            UserRoles.ID_FARMER -> {
                SubmitLoadRequest(
                    userId = userId,
                    propertyId = propertyId,
                    distrcitIds = listOf(1, 2),
                    message = message,
                    roleId = roleId,
                    henCount = cleanHenCount.toInt(),
                    henWeight = henSizeFloat,
                    loadAvailable = isActivate
                )
            }

            UserRoles.ID_TRADER, UserRoles.ID_SHOPKEEPER -> {
                SubmitLoadRequest(
                    userId = userId,
                    propertyId = propertyId,
                    distrcitIds = listOf(1, 2),
                    message = message,
                    roleId = roleId,
                    henCount = cleanHenCount.toInt(),
                    henWeight = henSizeFloat,
                    needLoad = isActivate
                )
            }

            else -> {
                /* SubmitLoadRequest(
                     userId = userId,
                     propertyId = propertyId,
                     distrcitIds = listOf(1, 2),
                     message = message,
                     roleId = roleId,
                     henCount = cleanHenCount.toInt(),
                     henWeight = henSizeFloat,
                     needLoad = isActivate
                 )*/
            }
        }

        //
        val call = when (roleId) {
            UserRoles.ID_FARMER -> {
                ApiClient.retrofit.create(ApiService::class.java).batchReady(request)
            }

            UserRoles.ID_TRADER -> {
                ApiClient.retrofit.create(ApiService::class.java).needLoad(request)
            }

            else -> {
                ApiClient.retrofit.create(ApiService::class.java).needLoad(request)
            }
        }


        ApiHelper.post(
            endpointCall = call,
            onSuccess = { response ->
                val ctx = activity?.applicationContext ?: return@post

                if (response.isSuccess) {
                    Toast.makeText(ctx, response.message, Toast.LENGTH_LONG).show()

                    if (isFromSubmit) {
                        if (roleId == UserRoles.ID_FARMER) {
                            sharedViewModel.setBatchReady(true)
                        } else {
                            sharedViewModel.setNeedLoad(true)
                        }


                        // 🔹 Submit clicked: disable Submit, enable ActivateAll
                        submitButton.isEnabled = false
                        submitButton.text = "Submitted (Wait 24 hrs)"
                        submitButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                        ActivateAllButton.isEnabled = true
                        ActivateAllButton.setBackgroundColor(Color.parseColor("#FF6347"))
                        SharedPreferencesManager.saveBatchBoolean(ctx, true)

                        SharedPreferencesManager.saveHenCountSubmitNeed(ctx, henCount)
                        SharedPreferencesManager.saveHenSizeSubmitNeed(ctx, henSizeFloat.toString())
                        if (roleId == UserRoles.ID_FARMER) {
                            sharedViewModel.setBatchReady(true)
                            (requireActivity() as? Dashboard)?.updateBatchReadySwitch(true)
                        } else {
                            sharedViewModel.setNeedLoad(true)
                            (requireActivity() as? Dashboard)?.updateBatchReadySwitch(true)
                        }
                        // ✅ Save pref + update sharedViewModel
                        when (roleId) {
                            UserRoles.ID_FARMER -> SharedPreferencesManager.saveLastSubmitTimeBatch(
                                ctx,
                                DateTimeUtils.formattedDateTime
                            )

                            UserRoles.ID_TRADER -> SharedPreferencesManager.saveLastSubmitTimeNeed(
                                ctx,
                                DateTimeUtils.formattedDateTime
                            )

                            else -> SharedPreferencesManager.saveLastSubmitTimeNeed(
                                ctx,
                                DateTimeUtils.formattedDateTime
                            )
                        }

                    } else {
                        // 🔹 ActivateAll clicked: disable ActivateAll, enable Submit
                        ActivateAllButton.isEnabled = false
                        ActivateAllButton.setBackgroundColor(Color.parseColor("#D3D3D3"))

                        submitButton.isEnabled = true
                        submitButton.text = "Submit"
                        submitButton.setBackgroundColor(Color.parseColor("#FF6347"))
                        if (roleId == UserRoles.ID_FARMER) {
                            sharedViewModel.setBatchReady(false)
                            (requireActivity() as? Dashboard)?.updateBatchReadySwitch(false)
                            SharedPreferencesManager.saveBatchBoolean(ctx, false)
                        } else {
                            sharedViewModel.setNeedLoad(false)
                            (requireActivity() as? Dashboard)?.updateBatchReadySwitch(false)
                            SharedPreferencesManager.saveNeedBoolean(ctx, false)

                        }
                        // Clear last submit time + saved hen data
                        when (roleId) {
                            UserRoles.ID_FARMER -> {
                                SharedPreferencesManager.clearLastSubmitTimeBatch(ctx)
                                SharedPreferencesManager.clearLastSubmitHensize(ctx)
                                SharedPreferencesManager.clearLastSubmitHencount(ctx)
                            }

                            UserRoles.ID_TRADER -> {
                                SharedPreferencesManager.clearLastSubmitTimeNeed(ctx)
                                SharedPreferencesManager.clearLastSubmitHensize(ctx)
                                SharedPreferencesManager.clearLastSubmitHencount(ctx)
                            }

                            else -> {
                                SharedPreferencesManager.clearLastSubmitTimeNeed(ctx)
                            }
                        }
                    }

                    henCountEditText.text.clear()
                    henSizeSpinner.setSelection(0)


                } else {
                    Log.e("SubmitLoad", "Failed: ${response.message}")
                    Toast.makeText(ctx, "Submit failed: ${response.message}", Toast.LENGTH_SHORT)
                        .show()
                }

                //         loader.hide()
            },
            onFailure = { error ->
                val ctx = activity?.applicationContext ?: return@post
                Log.e("SubmitLoad", "Error: $error")
                Toast.makeText(ctx, "Error: $error", Toast.LENGTH_SHORT).show()
//                loader.hide()
            }
        )
    }

    override fun onPause() {
        super.onPause()
        // Optional: save form progress if you want
        // Example: save hen count text temporarily
        if (SharedPreferencesManager.getBatchBoolean(requireContext()) == false || SharedPreferencesManager.getNeedBoolean(
                requireContext()
            ) == false
        ) {
            (requireActivity() as? Dashboard)?.updateBatchReadySwitch(null, false)

        }
    }
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}