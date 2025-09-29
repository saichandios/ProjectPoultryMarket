package com.gsggroups.poultrymarket.Common

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.gsggroups.poultrymarket.Common.ForceUpdateBottomSheet
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.base.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST

// Data class for API response
data class ApiVersionResponse(
    val latestVersion: String,
    val forceUpdate: Boolean
)

// Retrofit API interface
interface ApiServiceVersion {
    @POST("/api/Version/getVersion") // Replace with your actual endpoint
    fun getVersion(): Call<ApiVersionResponse>
}

object ForceUpdateManager {
    fun checkAndShowUpdateDialog(
        activity: FragmentActivity,
        loader: LoaderUtils,
        onForceUpdate: (() -> Unit)? = null
    ) {

        loader.show() // Show loader

        val call: Call<ApiVersionResponse> = ApiClient.retrofit
            .create(ApiServiceVersion::class.java)
            .getVersion() // No parameters needed

        call.enqueue(object : Callback<ApiVersionResponse> {
            override fun onResponse(
                call: Call<ApiVersionResponse>,
                response: Response<ApiVersionResponse>
            ) {
                loader.hide() // Hide loader

                if (response.isSuccessful && response.body() != null) {
                    val versionResponse = response.body()!!
                    val currentVersion = getCurrentAppVersion(activity)
                    val storeVersion = versionResponse.latestVersion
                    val forceUpdate = versionResponse.forceUpdate

                    if (isUpdateAvailable(currentVersion, storeVersion)  && forceUpdate) {
                        // Trigger force update callback
                        onForceUpdate?.invoke()

                        // Show bottom sheet popup for update
                        val bottomSheet = ForceUpdateBottomSheet(
                            latestVersion = storeVersion,
                            packageName = activity.packageName,
                            isForceUpdate = forceUpdate
                        )

                        // Make bottom sheet non-cancelable if forceUpdate is true
                        bottomSheet.isCancelable = !forceUpdate

                        bottomSheet.show(activity.supportFragmentManager, "ForceUpdateBottomSheet")
                    }
                } else {
                    Log.e("ForceUpdateManager", "API response failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiVersionResponse>, t: Throwable) {
                loader.hide()
                Log.e("ForceUpdateManager", "Version check failed: ${t.message}")
            }
        })
    }

    private fun getCurrentAppVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0"
        }
    }



    // Helper function to compare versions
    private fun isUpdateAvailable(currentVersion: String, storeVersion: String): Boolean {
        val currentParts = currentVersion.split(".")
        val storeParts = storeVersion.split(".")
        val maxLength = maxOf(currentParts.size, storeParts.size)

        for (i in 0 until maxLength) {
            val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
            val storePart = storeParts.getOrNull(i)?.toIntOrNull() ?: 0

            if (currentPart < storePart) return true
            if (currentPart > storePart) return false
        }
        return false // Versions are equal
    }
}
