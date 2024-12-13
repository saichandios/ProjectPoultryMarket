package com.gsggroups.poultrymarket.Common

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    // Create a TrustManager that accepts all certificates
    val trustAllCertificates: X509TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    // Create SSLContext that uses the TrustManager to accept all certificates
    val sslContext: SSLContext = try {
        SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustAllCertificates), java.security.SecureRandom())
        }
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException("Error creating SSLContext", e)
    } catch (e: KeyManagementException) {
        throw RuntimeException("Error initializing SSLContext", e)
    }

    val okHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustAllCertificates)
        .hostnameVerifier { _, _ -> true }
        .build()

    const val BASE_URL = "http://localhost:16490/api/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

}




interface ApiService {

    @GET
    fun getData(@Url url: String, @QueryMap params: Map<String, String>): Call<Any>

    @POST
    fun postData(@Url url: String, @Body body: Any): Call<Any>

    @PATCH
    fun patchData(@Url url: String, @Body body: Any): Call<Any>
}