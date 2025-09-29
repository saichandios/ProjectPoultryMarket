//package com.gsggroups.poultrymarket
//
//import android.content.Context
//import android.content.pm.PackageManager
//import android.util.Log
//import android.widget.Toast
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.UpdateAvailability
//
//class AppUpdateChecker(private val context: Context) {
//
//    private val TAG = "AppUpdateChecker"
//
//    fun checkForUpdate() {
//        val currentVersion = getCurrentAppVersion()
//        Log.d(TAG, "Current app version: $currentVersion")
//
//        val appUpdateManager = AppUpdateManagerFactory.create(context)
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
//            ) {
//                // New version is available
//                val storeVersion = appUpdateInfo.availableVersionCode().toString()
//                Log.d(TAG, "Play Store version available: $storeVersion")
//
//                Toast.makeText(
//                    context,
//                    "New version available! Please update the app.",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
//                Log.d(TAG, "App is up to date")
//            }
//        }
//
//        appUpdateInfoTask.addOnFailureListener { exception ->
//            Log.e(TAG, "Failed to check update", exception)
//        }
//    }
//
//    private fun getCurrentAppVersion(): String {
//        return try {
//            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
//            pInfo.versionName ?: "1.0"
//        } catch (e: PackageManager.NameNotFoundException) {
//            "1.0"
//        }
//    }
//}
