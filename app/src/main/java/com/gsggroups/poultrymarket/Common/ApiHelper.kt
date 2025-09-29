package com.gsggroups.poultrymarket.Common

import android.util.Log
import com.gsggroups.poultrymarket.Utils.ApiResponse
import com.gsggroups.poultrymarket.Utils.ApiResponseNew
import com.gsggroups.poultrymarket.Utils.ApiVersion
import com.gsggroups.poultrymarket.base.ApiClient
import org.json.JSONObject
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


    private val service = ApiClient.retrofit.create(ApiService::class.java)

    fun <T> post(
        endpointCall: Call<ApiResponse<T>>,
        onSuccess: (ApiResponse<T>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        endpointCall.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(
                call: Call<ApiResponse<T>>,
                response: Response<ApiResponse<T>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = JSONObject(errorBody ?: "")
                        errorJson.optString("message", response.message()) // fallback to generic message
                    } catch (e: Exception) {
                        e.printStackTrace()
                        response.message()
                    }
                    onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse<T>>, t: Throwable) {
                onFailure(t.localizedMessage ?: "Something went wrong")
            }
        })
    }
    fun <T> postNew(
        endpointCall: Call<ApiResponseNew<T>>,
        onSuccess: (ApiResponseNew<T>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        endpointCall.enqueue(object : Callback<ApiResponseNew<T>> {
            override fun onResponse(
                call: Call<ApiResponseNew<T>>,
                response: Response<ApiResponseNew<T>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        val errorJson = JSONObject(errorBody ?: "")
                        errorJson.optString("message", response.message()) // fallback to generic message
                    } catch (e: Exception) {
                        e.printStackTrace()
                        response.message()
                    }
                    onFailure(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponseNew<T>>, t: Throwable) {
                onFailure(t.localizedMessage ?: "Something went wrong")
            }
        })
    }

//    fun <T> postApiVersion(
//        apiCall: Call<ApiVersion<T>>,
//        onSuccess: (ApiVersion<T>) -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        apiCall.enqueue(object : Callback<ApiVersion<T>> {
//            override fun onResponse(
//                call: Call<ApiVersion<T>>,
//                response: Response<ApiVersion<T>>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    onSuccess(response.body()!!)
//                } else {
//                    val errorMsg = try {
//                        val errorBody = response.errorBody()?.string()
//                        val errorJson = JSONObject(errorBody ?: "")
//                        errorJson.optString("message", response.message()) // fallback to generic message
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        response.message()
//                    }
//                    onFailure(errorMsg ?: "Unknown error")
//                }
//            }
//
//            override fun onFailure(call: Call<ApiVersion<T>>, t: Throwable) {
//                onFailure(t.localizedMessage ?: "Something went wrong")
//            }
//        })
//    }
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

//object ApiHelper {
//
//    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
//
//    fun get(
//        url: String,
//        params: Map<String, String> = emptyMap(),
//        onSuccess: (Map<String, Any>) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        val call = apiService.getData(url, params)
//        call.enqueue(object : Callback<Map<String, Any>> {
//            override fun onResponse(
//                call: Call<Map<String, Any>>,
//                response: Response<Map<String, Any>>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    onSuccess(response.body()!!)
//                } else {
//                    onError("Error ${response.code()}: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
//                Log.e("SimpleApiHelper", "GET failed: ${t.message}")
//                onError(t.message ?: "Unknown error")
//            }
//        })
//    }
//
//    fun post(
//        url: String,
//        body: Any,
//        onSuccess: (Map<String, Any>) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        val call = apiService.postData(url, body)
//        call.enqueue(object : Callback<Map<String, Any>> {
//            override fun onResponse(
//                call: Call<Map<String, Any>>,
//                response: Response<Map<String, Any>>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    onSuccess(response.body()!!)
//                } else {
//                    onError("Error ${response.code()}: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
//                Log.e("SimpleApiHelper", "POST failed: ${t.message}")
//                onError(t.message ?: "Unknown error")
//            }
//        })
//    }
//}



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