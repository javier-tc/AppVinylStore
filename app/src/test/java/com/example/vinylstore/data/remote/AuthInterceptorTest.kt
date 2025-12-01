package com.example.vinylstore.data.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    private lateinit var sessionManager: SessionManager
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain
    
    @Before
    fun setup() {
        sessionManager = mockk(relaxed = true)
        authInterceptor = AuthInterceptor(sessionManager)
        chain = mockk(relaxed = true)
    }
    
    @Test
    fun `intercept añade header Authorization cuando hay token`() {
        val originalRequest = Request.Builder()
            .url("https://example.com/api/test")
            .build()
        
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        
        every { sessionManager.getToken() } returns "test-token-123"
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response
        
        val result = authInterceptor.intercept(chain)
        
        verify { 
            chain.proceed(match { request ->
                request.header("Authorization") == "Bearer test-token-123"
            })
        }
        assertEquals(200, result.code)
    }
    
    @Test
    fun `intercept no añade header Authorization cuando no hay token`() {
        val originalRequest = Request.Builder()
            .url("https://example.com/api/test")
            .build()
        
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        
        every { sessionManager.getToken() } returns null
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response
        
        val result = authInterceptor.intercept(chain)
        
        verify { 
            chain.proceed(match { request ->
                request.header("Authorization") == null
            })
        }
        assertEquals(200, result.code)
    }
    
    @Test
    fun `intercept preserva otros headers cuando añade Authorization`() {
        val originalRequest = Request.Builder()
            .url("https://example.com/api/test")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()
        
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        
        every { sessionManager.getToken() } returns "test-token"
        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns response
        
        authInterceptor.intercept(chain)
        
        verify { 
            chain.proceed(match { request ->
                request.header("Authorization") == "Bearer test-token" &&
                request.header("Content-Type") == "application/json" &&
                request.header("Accept") == "application/json"
            })
        }
    }
}

