package com.gsggroups.poultrymarket.Common

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {
    fun checkNotificationStatus(context: Context) {
        if (!areNotificationsEnabled(context)) {
            AlertDialog.Builder(context)
                .setTitle("Notifications Disabled")
                .setMessage("App notifications are turned off. Please enable them in settings to receive updates.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    openNotificationSettings(context)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.areNotificationsEnabled()
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    private fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        }
        context.startActivity(intent)
    }
}
