package com.gsggroups.poultrymarket

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

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

        // Handle click on "Add Farm" link
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
}