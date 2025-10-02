package com.gsggroups.poultrymarket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Common.ApiServiceVersion
import com.gsggroups.poultrymarket.Common.LoaderUtils
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.DBManager.State
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.UserItem
import com.gsggroups.poultrymarket.base.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private val SPLASH_TIME: Long = 0
    private val TAG = "FirestoreLogs"
    val gson = Gson()
    var getstoreVersion = ""
    var getforceUpdate = false
    private val loader = LoaderUtils(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash)

    }
    private fun navigateToNextScreen() {
        val signInStatus = SharedPreferencesManager.getSignedIn(this)
        if (signInStatus) {
            startActivity(Intent(this, Dashboard::class.java))
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        finish()
    }


    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            val versionInfo = retryApiCall(
                maxRetries = 3,          // retry 3 times
                delayMillis = 2000L      // 2 sec wait between retries
            ) {
                withContext(Dispatchers.IO) {
                    val okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)   // 🔹 allow long server response
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build()

                    val retrofit = ApiClient.retrofit.newBuilder()
                        .client(okHttpClient)
                        .build()

                    val response = retrofit.create(ApiServiceVersion::class.java).getVersion().execute()
                    if (response.isSuccessful) response.body() else null
                }
            }

            // ✅ Hide loader only after API finishes (success OR fail)
            if (!isFinishing) loader.hide()

            // ✅ Handle result AFTER waiting for response
            if (versionInfo != null) {
                val currentVersion = getCurrentAppVersion()
                val storeVersion = versionInfo.latestVersion
                val forceUpdate = versionInfo.forceUpdate

                val updateAvailable = isUpdateAvailable(currentVersion, storeVersion)

                if (updateAvailable && forceUpdate) {
                    // show bottom sheet, block navigation
                    showForceUpdateBottomSheet(storeVersion, packageName, true)
                } else {
                    // no update needed → navigate
                    navigateToNextScreen()
                }
            } else {
                // retries failed → safe navigation
                navigateToNextScreen()
            }
        }
    }


    private fun checkFireStoreFile() {
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("teststate").document("teststateD")
            .collection("testdist").document("testdist")
            .collection("testcity").document("testcity")
            .collection(UserRoles.ROLE_FARMER)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty()) {
//                    val farmData = document.toObject(Farmer::class.java)
//                    println("Farm data: $farmData")
//
//                    val json = gson.toJson(farmData)
//                    println("Farm data in JSON format: $json")
                    val farmersList = mutableListOf<UserItem>()
                    for (document in documents) {
                        println("Document ID: ${document.id} => ${document.data}")
                        val farmer = document.toObject(UserItem::class.java)
                        farmer.let { farmersList.add(it) }
                    }
                    val json = gson.toJson(farmersList)
                    println("Docum:::: ${json}")
                } else {
                    println("Collection exists but has no documents. ${documents}")
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching collection: ${exception.message}")
            }
    }

    private fun uploadJsonToFirestore() {
        val assetManager = assets
        val inputStream = assetManager.open("states.json")
        val reader = InputStreamReader(inputStream)

        // Define the type of your JSON array
        val stateListType = object : TypeToken<List<State>>() {}.type
        val stateList: List<State> = Gson().fromJson(reader, stateListType)

        // Upload each state as a document in a Firestore collection
        stateList.forEach { state ->
            firestore.collection("states")
                .document(state.stateId)
                .set(state)
                .addOnSuccessListener {
                    println("DocumentSnapshot added with ID: ${state.stateId}")
                }
                .addOnFailureListener { e ->
                    println("Error adding document $e")
                }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        loader ?. let {
            if (it.loaderDialog?.isShowing == true) it.hide()
        }
    }


private suspend fun <T> retryApiCall(
        maxRetries: Int,
        delayMillis: Long,
        block: suspend () -> T?
    ): T? {
        repeat(maxRetries - 1) { attempt ->
            val result = block()
            if (result != null) return result
            Log.w("SplashActivity", "API call failed, retrying... (${attempt + 1}/$maxRetries)")
            delay(delayMillis)
        }
        return block() // last attempt
    }


    private fun getCurrentAppVersion(): String {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo . versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    private fun isUpdateAvailable(currentVersion: String, storeVersion: String): Boolean {
        val currentParts = currentVersion.split(".")
        val storeParts = storeVersion.split(".")
        val maxLength = maxOf(currentParts.size, storeParts.size)
        for (i in 0 until maxLength) {
            val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
            val storePart = storeParts.getOrNull(i)?.toIntOrNull()
                ?: 0
            if (currentPart < storePart) return true
            if (currentPart > storePart) return false
        }
        return false
    }

    private fun showForceUpdateBottomSheet(
        latestVersion: String,
        packageName: String,
        isForceUpdate: Boolean
    ) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(
            R.layout.force_update_bottom_sheet,
            null
        )
        bottomSheetDialog.setContentView(bottomSheetView)

        val tvMessage = bottomSheetView.findViewById<TextView>(R.id.tvMessage)
        val btnUpdate = bottomSheetView.findViewById<Button>(R.id.btnUpdate)

        tvMessage.text = "A new version ($latestVersion) is available!"

        btnUpdate.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
            if (isForceUpdate) finish() else bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setCancelable(!isForceUpdate)

        // ✅ Show safely on main thread
        runOnUiThread {
            if (!isFinishing) bottomSheetDialog.show()
        }
    }

}