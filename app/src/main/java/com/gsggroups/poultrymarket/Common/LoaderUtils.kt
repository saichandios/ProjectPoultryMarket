package com.gsggroups.poultrymarket.Common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.gsggroups.poultrymarket.R

class LoaderUtils(private val context: Context) {

    var loaderDialog: Dialog? = null
    private var ivClose: ImageView? = null

    // Show the loader
    fun show(showCloseIcon: Boolean = false) {
        if (loaderDialog == null) {
            loaderDialog = Dialog(context).apply {
                val view = LayoutInflater.from(context).inflate(R.layout.loader_layout, null)

                ivClose = view.findViewById(R.id.ivClose)
                ivClose?.setOnClickListener {
                    loaderDialog?.let { dialog ->
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                    }
                }

                setContentView(view)
                setCancelable(false)
                setCanceledOnTouchOutside(false)

                val sizePx = 150.dpToPx(context) // 150dp square
                window?.setLayout(sizePx, sizePx)
            }
        }

        // Control close icon visibility
        ivClose?.visibility = if (showCloseIcon) View.VISIBLE else View.GONE

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
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    // Dismiss helper (used by close button)
    private fun dismiss() {
        loaderDialog?.dismiss()
    }

}