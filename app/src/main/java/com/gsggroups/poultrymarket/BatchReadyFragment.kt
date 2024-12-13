package com.gsggroups.poultrymarket

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
class BatchReadyFragment : Fragment() {

    private lateinit var loadAvilableText: TextView
    private lateinit var load_available_switch: Switch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAvilableText = view.findViewById(R.id.load_available_text)
        load_available_switch = view.findViewById(R.id.load_available_switch)

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

        val editTextSalary = view.findViewById<EditText>(R.id.text_hen_count)
        formatIndianCurrency(editTextSalary)
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
}