package com.example.vinylstore.data.remote.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

// DTOs para la API de Last.fm
data class LastFmResponse(
    @SerializedName("tracks")
    val tracks: TracksWrapper?
)

data class TracksWrapper(
    @SerializedName("track")
    @JsonAdapter(TrackDeserializer::class)
    val track: List<TrackDto>?
)

// Deserializador personalizado para manejar track que puede ser objeto único o lista
class TrackDeserializer : JsonDeserializer<List<TrackDto>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<TrackDto> {
        if (json == null || json.isJsonNull) {
            return emptyList()
        }
        
        if (json.isJsonArray) {
            // Es una lista
            return json.asJsonArray.mapNotNull { element ->
                try {
                    context?.deserialize<TrackDto>(element, TrackDto::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        } else if (json.isJsonObject) {
            // Es un objeto único
            return try {
                listOfNotNull(context?.deserialize<TrackDto>(json, TrackDto::class.java))
            } catch (e: Exception) {
                emptyList()
            }
        }
        
        return emptyList()
    }
}

data class TrackDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("artist")
    val artist: ArtistInfo,
    @SerializedName("image")
    val images: List<ImageDto>?,
    @SerializedName("url")
    val url: String?
)

data class ArtistInfo(
    @SerializedName("name")
    val name: String
)

data class ImageDto(
    @SerializedName("#text")
    val url: String,
    @SerializedName("size")
    val size: String
)

// Modelo simplificado para la UI
data class MusicRecommendation(
    val trackName: String,
    val artistName: String,
    val imageUrl: String,
    val url: String?
)

