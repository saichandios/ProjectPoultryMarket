package com.gsggroups.poultrymarket.Common

import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ApiHelper {

    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

    // Generic GET request
    fun <T> get(
        url: String,
        params: Map<String, String> = emptyMap(),
        responseType: Class<T>,
        onSuccess: (response: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val call = apiService.getData(url, params)
        handleApiCall(call, responseType, onSuccess, onFailure)
    }

    // Generic POST request
    fun <T> post(
        url: String,
        body: Any,
        responseType: Class<T>,
        onSuccess: (response: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val call = apiService.postData(url, body)
        handleApiCall(call, responseType, onSuccess, onFailure)
    }

    // Common handler for all API calls
    private fun <T> handleApiCall(
        call: Call<Any>,
        responseType: Class<T>,
        onSuccess: (response: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    try {
                        val json = response.body().toString()
                        val apiResponse = Gson().fromJson(json, responseType)
                        onSuccess(apiResponse)
                    } catch (e: Exception) {
                        onFailure("JSON Parsing Error: ${e.localizedMessage}")
                    }
                } else {
                    onFailure("Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("ApiHelper", "API call failed: ${t.message}")
                onFailure(t.message ?: "Unknown error")
            }
        })
    }
}



/*
            ApiHelper.get(
                url = "https://api.yourserver.com/items",
                params = mapOf("type" to "chicks", "status" to "available"),
                onSuccess = { response ->
                    // Handle success
                    Log.d("MainActivity", "Response: $response")
                },
                onFailure = { error ->
                    // Handle error
                    Log.e("MainActivity", "Error: $error")
                }
            )

               val requestBody = mapOf("name" to "Farmer", "location" to "CityX")
                ApiHelper.post(
                    url = "https://api.yourserver.com/addItem",
                    body = requestBody,
                    onSuccess = { response ->
                        // Handle success
                        Log.d("MainActivity", "Response: $response")
                    },
                    onFailure = { error ->
                        // Handle error
                        Log.e("MainActivity", "Error: $error")
                    }
                )



                        val patchBody = mapOf("status" to "sold")
                        ApiHelper.patch(
                            url = "https://api.yourserver.com/updateItem/123",
                            body = patchBody,
                            onSuccess = { response ->
                                // Handle success
                                Log.d("MainActivity", "Response: $response")
                            },
                            onFailure = { error ->
                                // Handle error
                                Log.e("MainActivity", "Error: $error")
                            }
                        )
*/