package com.gsggroups.poultrymarket.Common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.gsggroups.poultrymarket.R

class LoaderUtils(private val context: Context) {

    private var loaderDialog: Dialog? = null

    // Show the loader
    fun show() {
        if (loaderDialog == null) {
            loaderDialog = Dialog(context).apply {
                val view = LayoutInflater.from(context).inflate(R.layout.loader_layout, null)
                setContentView(view)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        loaderDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    // Hide the loader
    fun hide() {
        loaderDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}