package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.MusicApi
import com.example.vinylstore.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody as OkHttpResponseBody

class MusicRepositoryTest {
    private lateinit var musicApi: MusicApi
    private lateinit var repository: MusicRepository
    
    @Before
    fun setup() {
        musicApi = mockk()
        repository = MusicRepository(musicApi, "test-api-key")
    }
    
    @Test
    fun `getTopTracks retorna recomendaciones exitosamente`() = runTest {
        val tracksResponse = LastFmResponse(
            tracks = TracksWrapper(
                track = listOf(
                    TrackDto(
                        name = "Track 1",
                        artist = ArtistInfo(name = "Artist 1"),
                        images = listOf(
                            ImageDto(url = "url1", size = "large")
                        ),
                        url = "link1"
                    )
                )
            )
        )
        val response = Response.success(tracksResponse)
        coEvery { musicApi.getTopTracks(any(), any(), any()) } returns response
        
        val result = repository.getTopTracks(5, 1)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `getTopTracks maneja errores correctamente`() = runTest {
        val response = Response.error<LastFmResponse>(400, OkHttpResponseBody.create(null, ""))
        coEvery { musicApi.getTopTracks(any(), any(), any()) } returns response
        
        val result = repository.getTopTracks(5, 1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getTracksByTag retorna tracks filtrados por tag`() = runTest {
        val tracksResponse = LastFmResponse(
            tracks = TracksWrapper(
                track = listOf(
                    TrackDto(
                        name = "Rock Track",
                        artist = ArtistInfo(name = "Rock Artist"),
                        images = listOf(
                            ImageDto(url = "url1", size = "large")
                        ),
                        url = "link1"
                    )
                )
            )
        )
        val response = Response.success(tracksResponse)
        coEvery { musicApi.getTracksByTag(any(), any(), any(), any()) } returns response
        
        val result = repository.getTracksByTag("rock", 5, 1)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
}

