package com.gsggroups.poultrymarket.Utils

import LoginResponse
import com.gsggroups.poultrymarket.Model.GetUserList
import com.gsggroups.poultrymarket.Model.ListResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/registerUser")
    fun registerUser(@Body request: Any): Call<ApiResponseNew<UserItem>>


    @POST("/loginUser")
    fun loginUser(@Body request: Any): Call<ApiResponse<UserItem>>

    @POST("/getUserList")
    fun getUserList(@Body request: GetUserList): Call<ApiResponse<ListResponseModel>>

    @POST("/changePin")
    fun changePin(@Body request: Any): Call<ApiResponse<Any>>

    @POST("/batchReady")
    fun batchReady(@Body request: Any): Call<ApiResponse<Any>>

 @POST("/needLoad")
    fun needLoad(@Body request: Any): Call<ApiResponse<Any>>

    @POST("/goingForLoad")
    fun goingForLoad(@Body request: Any): Call<ApiResponse<Any>>

    // Add other API methods here
}