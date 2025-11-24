package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.MusicApi
import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.data.remote.dto.TrackDto

class MusicRepository(
    private val musicApi: MusicApi,
    private val apiKey: String
) {
    suspend fun getTopTracks(limit: Int = 5): Result<List<MusicRecommendation>> {
        return try {
            val response = musicApi.getTopTracks(
                apiKey = apiKey,
                limit = limit
            )
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()!!.tracks?.track ?: emptyList()
                val recommendations = tracks.map { it.toMusicRecommendation() }
                Result.success(recommendations)
            } else {
                Result.failure(Exception("Error al obtener recomendaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTracksByTag(tag: String, limit: Int = 5): Result<List<MusicRecommendation>> {
        return try {
            val response = musicApi.getTracksByTag(
                tag = tag,
                apiKey = apiKey,
                limit = limit
            )
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()!!.tracks?.track ?: emptyList()
                val recommendations = tracks.map { it.toMusicRecommendation() }
                Result.success(recommendations)
            } else {
                Result.failure(Exception("Error al obtener recomendaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun TrackDto.toMusicRecommendation(): MusicRecommendation {
        // Buscar la imagen de tamaño medium o large
        val imageUrl = images?.find { it.size == "medium" || it.size == "large" }?.url
            ?: images?.firstOrNull()?.url
            ?: "https://via.placeholder.com/300"
        
        return MusicRecommendation(
            trackName = name,
            artistName = artist.name,
            imageUrl = imageUrl,
            url = url
        )
    }
}

