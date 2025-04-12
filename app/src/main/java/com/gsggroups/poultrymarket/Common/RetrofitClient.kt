package com.gsggroups.poultrymarket.Common

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url
import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    // Base URL for your API
    const val BASE_URL = "https://poultrymarket.xyz/"

    // OkHttpClient without SSL configurations (default settings)
    private val okHttpClient = OkHttpClient.Builder()
        .build()  // No custom SSL handling

    // Retrofit instance
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use default OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // Gson converter for JSON parsing
            .build()
    }
}


interface ApiService {

    @GET
    fun <T> getData(@Url url: String, @QueryMap params: Map<String, String>): Call<T>

    @POST
    fun <T> postData(@Url url: String, @Body body: Any): Call<T>
}
