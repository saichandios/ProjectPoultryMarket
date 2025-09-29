package com.gsggroups.poultrymarket.Common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gsggroups.poultrymarket.R

class ForceUpdateBottomSheet (
    private val latestVersion: String,
    private val packageName: String,
    private val isForceUpdate: Boolean
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.force_update_bottom_sheet, container, false)

        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)

        tvMessage.text = "A new version ($latestVersion) is available!"

        btnUpdate.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            startActivity(intent)

            if (isForceUpdate) {
                activity?.finish() // Close app if update is forced
            } else {
                dismiss() // Just close bottom sheet if optional
            }
        }

        return view
    }
}
