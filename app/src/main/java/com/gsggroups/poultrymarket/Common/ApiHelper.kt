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
        // Explicitly specify the response type in the API call
        val call: Call<T> = apiService.getData(url, params)
        handleApiCall(call, responseType, onSuccess, onFailure)
    }


    // Generic POST request
    // Generic POST request
    fun <T> post(
        url: String,
        body: Any,
        responseType: Class<T>,
        onSuccess: (response: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        // Call the POST method from ApiService, passing the body and URL
        val call: Call<T> = apiService.postData(url, body)
        handleApiCall(call, responseType, onSuccess, onFailure)
    }


    // Common handler for all API calls
    private fun <T> handleApiCall(
        call: Call<T>,  // Ensure the call is typed with T
        responseType: Class<T>,
        onSuccess: (response: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        onSuccess(body)  // Pass the parsed response
                    } else {
                        onFailure("Response body is null")
                    }
                } else {
                    onFailure("Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
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