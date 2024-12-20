package com.gsggroups.poultrymarket.Common

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class NetworkManager {

    private val client = OkHttpClient()

    fun getNearbyDistricts(lat: Double, lon: Double, callback: (List<String>) -> Unit) {
        // Overpass API URL
//        val overpassUrl = "http://overpass-api.de/api/interpreter"

        val radius = 50000

        // OpenStreetMap Overpass API URL
        val overpassUrl = "http://overpass-api.de/api/interpreter?data=[out:json];(relation[\"admin_level\"=\"5\"](around:$radius,$lat,$lon););out body;"

        val request = Request.Builder()
            .url(overpassUrl)
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkManager", "Error: ${e.message}")
                callback(emptyList()) // Returning empty list on failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }

                    // Parse the response to extract district names
                    val districts = mutableListOf<String>()
                    val elements = jsonResponse?.getJSONArray("elements")

                    if (elements != null) {
                        for (i in 0 until elements.length()) {
                            val element = elements.getJSONObject(i)
                            val tags = element.optJSONObject("tags")
                            val districtName = tags?.optString("name", "Unknown")
                            if (districtName != "Unknown") {
                                if (districtName != null) {
                                    districts.add(districtName)
                                    println(districtName)
                                }
                            }
                        }
                        println(districts)
                    }
                    // Return the districts to the callback
                    callback(districts)
                } else {
                    Log.e("NetworkManager", "Failed to fetch data: ${response.message}")
                    callback(emptyList()) // Returning empty list on failure
                }
            }
        })
    }
}