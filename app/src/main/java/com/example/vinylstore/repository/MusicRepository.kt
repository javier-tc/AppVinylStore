package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.MusicApi
import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.data.remote.dto.TrackDto

class MusicRepository(
    private val musicApi: MusicApi,
    private val apiKey: String
) {
    suspend fun getTopTracks(limit: Int = 5, page: Int = 1): Result<List<MusicRecommendation>> {
        return try {
            val response = musicApi.getTopTracks(
                apiKey = apiKey,
                limit = limit,
                page = page
            )
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()!!.tracks?.track ?: emptyList()
                // Filtrar tracks que no tengan nombre o artista válido
                val validTracks = tracks.filter { 
                    it.name.isNotBlank() && it.artist?.name?.isNotBlank() == true
                }
                val recommendations = validTracks.map { it.toMusicRecommendation() }
                Result.success(recommendations)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                val errorMessage = "Error ${response.code()}: ${response.message()}. $errorBody"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    suspend fun getTracksByTag(tag: String, limit: Int = 5, page: Int = 1): Result<List<MusicRecommendation>> {
        return try {
            val response = musicApi.getTracksByTag(
                tag = tag,
                apiKey = apiKey,
                limit = limit,
                page = page
            )
            if (response.isSuccessful && response.body() != null) {
                val tracks = response.body()!!.tracks?.track ?: emptyList()
                val recommendations = tracks.map { it.toMusicRecommendation() }
                Result.success(recommendations)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                val errorMessage = "Error ${response.code()}: ${response.message()}. $errorBody"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    private fun TrackDto.toMusicRecommendation(): MusicRecommendation {
        // Intentar obtener la imagen más grande disponible
        // Prioridad: extralarge > large > medium > small > cualquiera
        val validImages = images?.filter { it.url.isNotBlank() } ?: emptyList()
        
        val imageUrl = validImages.find { it.size == "extralarge" }?.url
            ?: validImages.find { it.size == "large" }?.url
            ?: validImages.find { it.size == "medium" }?.url
            ?: validImages.firstOrNull()?.url
            ?: "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png" // Placeholder genérico de last.fm o similar
        
        return MusicRecommendation(
            trackName = name.ifBlank { "Sin título" },
            artistName = artist?.name?.ifBlank { "Artista desconocido" } ?: "Artista desconocido",
            imageUrl = imageUrl,
            url = url
        )
    }
}
