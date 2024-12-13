package com.gsggroups.poultrymarket.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.gsggroups.poultrymarket.databinding.ActivityDialogCustomAlertBinding

class CustomAlertDialog(private val context: Context) {

    // Use View Binding to bind the XML layout to this Kotlin class
    private val binding: ActivityDialogCustomAlertBinding =
        ActivityDialogCustomAlertBinding.inflate(LayoutInflater.from(context))
    private val alertDialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root) // Set the custom layout to the dialog
        alertDialog = builder.create() // Create the AlertDialog
    }

    // Function to set the title of the dialog
    fun setTitle(title: String): CustomAlertDialog {
        binding.tvTitle.text = title
        return this
    }

    // Function to set the description of the dialog
    fun setDescription(description: String): CustomAlertDialog {
        binding.tvDescription.text = description
        return this
    }

    // Function to control OK button visibility and action
    fun showOkButton(show: Boolean, okText: String = "OK", onOkClick: (() -> Unit)? = null): CustomAlertDialog {
        if (show) {
            binding.btnOk.text = okText
            binding.btnOk.setOnClickListener {
                onOkClick?.invoke()
                alertDialog.dismiss()
            }
            binding.btnOk.visibility = View.VISIBLE
        } else {
            binding.btnOk.visibility = View.GONE
        }
        return this
    }

    // Function to control Cancel button visibility and action
    fun showCancelButton(show: Boolean, cancelText: String = "Cancel", onCancelClick: (() -> Unit)? = null): CustomAlertDialog {
        if (show) {
            binding.btnCancel.text = cancelText
            binding.btnCancel.setOnClickListener {
                onCancelClick?.invoke()
                alertDialog.dismiss()
            }
            binding.btnCancel.visibility = View.VISIBLE
        } else {
            binding.btnCancel.visibility = View.GONE
        }
        return this
    }

    // Show the dialog
    fun show() {
        alertDialog.show()
    }

    // Dismiss the dialog
    fun dismiss() {
        alertDialog.dismiss()
    }
}

/*
             val alert = CustomAlertDialog(this)
                .setTitle("Information")
                .setDescription("This is a description of the alert.")
                .showOkButton(true, "OK") {
                    // Handle OK button click
                    Log.d("MainActivity", "OK clicked")
                }
                .showCancelButton(false) // Hide cancel button
                .show() // Finally, show the alert dialog


                   val alert = CustomAlertDialog(this)
                    .setTitle("Warning")
                    .setDescription("Are you sure you want to proceed?")
                    .showOkButton(true, "Yes") {
                        // Handle OK button click
                        Log.d("MainActivity", "Yes clicked")
                    }
                    .showCancelButton(true, "No") {
                        // Handle Cancel button click
                        Log.d("MainActivity", "No clicked")
                    }
                    .show()
*/