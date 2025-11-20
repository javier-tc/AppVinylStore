package com.example.vinylstore.network

import com.example.vinylstore.network.api.AuthApi
import com.example.vinylstore.network.api.CartApi
import com.example.vinylstore.network.api.ProductApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    //URL base del backend
    //10.0.2.2 es la dirección del emulador Android para acceder a localhost
    //Para dispositivos físicos, usar la IP de tu máquina en la red local (ej: "http://192.168.1.100:8080/")
    private const val BASE_URL = "http://10.0.2.2:8080/"
    
    private fun createOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private fun createRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(sessionManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    fun createAuthApi(sessionManager: SessionManager): AuthApi {
        return createRetrofit(sessionManager).create(AuthApi::class.java)
    }
    
    fun createProductApi(sessionManager: SessionManager): ProductApi {
        return createRetrofit(sessionManager).create(ProductApi::class.java)
    }
    
    fun createCartApi(sessionManager: SessionManager): CartApi {
        return createRetrofit(sessionManager).create(CartApi::class.java)
    }
}

