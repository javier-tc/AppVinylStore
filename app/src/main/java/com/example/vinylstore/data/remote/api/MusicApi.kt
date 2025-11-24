package com.example.vinylstore.data.remote.api

import com.example.vinylstore.data.remote.dto.LastFmResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {
    // Obtener los tracks más populares del momento
    @GET("2.0/")
    suspend fun getTopTracks(
        @Query("method") method: String = "chart.gettoptracks",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): Response<LastFmResponse>
    
    // Obtener tracks por tag (género)
    @GET("2.0/")
    suspend fun getTracksByTag(
        @Query("method") method: String = "tag.gettoptracks",
        @Query("tag") tag: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): Response<LastFmResponse>
}

