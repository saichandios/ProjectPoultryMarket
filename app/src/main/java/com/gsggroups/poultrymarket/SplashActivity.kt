package com.gsggroups.poultrymarket

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.gsggroups.poultrymarket.Common.SharedPreferencesManager
import com.gsggroups.poultrymarket.Common.UserRoles
import com.gsggroups.poultrymarket.DBManager.State
import com.gsggroups.poultrymarket.DashboardView.Dashboard
import com.gsggroups.poultrymarket.Model.UserItem
import java.io.InputStreamReader

class SplashActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private val SPLASH_TIME: Long = 1000
    private val TAG = "FirestoreLogs"
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



//                 uploadJsonToFirestore()
        
            //Delay
            val signInStatus = SharedPreferencesManager.getSignedIn(this)
        if (signInStatus) {
            Handler().postDelayed({
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            }, SPLASH_TIME)
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }, SPLASH_TIME)
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

}