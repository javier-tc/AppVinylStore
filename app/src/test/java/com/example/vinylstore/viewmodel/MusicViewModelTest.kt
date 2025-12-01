package com.example.vinylstore.viewmodel

import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.repository.MusicRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MusicViewModelTest {
    private lateinit var musicRepository: MusicRepository
    private lateinit var viewModel: MusicViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        musicRepository = mockk()
        viewModel = MusicViewModel(musicRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadTopTracks carga recomendaciones exitosamente`() = runTest {
        val recommendations = listOf(
            MusicRecommendation("Track 1", "Artist 1", "url1", "link1"),
            MusicRecommendation("Track 2", "Artist 2", "url2", "link2")
        )
        
        coEvery { musicRepository.getTopTracks(any(), any()) } returns Result.success(recommendations)
        
        viewModel.loadTopTracks()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { musicRepository.getTopTracks(10, any()) }
    }
    
    @Test
    fun `loadTopTracks maneja errores correctamente`() = runTest {
        coEvery { musicRepository.getTopTracks(any(), any()) } returns Result.failure(Exception("Error de red"))
        
        viewModel.loadTopTracks()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val error = viewModel.error.value
        assertNotNull(error)
        assertTrue(error!!.contains("Error"))
    }
    
    @Test
    fun `loadTracksByTag carga tracks por tag`() = runTest {
        val recommendations = listOf(
            MusicRecommendation("Track 1", "Artist 1", "url1", "link1")
        )
        
        coEvery { musicRepository.getTracksByTag(any(), any(), any()) } returns Result.success(recommendations)
        
        viewModel.loadTracksByTag("rock")
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { musicRepository.getTracksByTag("rock", 5, any()) }
    }
    
    @Test
    fun `loadTracksByTag maneja errores correctamente`() = runTest {
        coEvery { musicRepository.getTracksByTag(any(), any(), any()) } returns Result.failure(Exception("Error de red"))
        
        viewModel.loadTracksByTag("rock")
        testDispatcher.scheduler.advanceUntilIdle()
        
        val error = viewModel.error.value
        assertNotNull(error)
        assertTrue(error!!.contains("Error"))
    }
    
    @Test
    fun `loadTopTracks establece isLoading correctamente`() = runTest {
        val recommendations = listOf(
            MusicRecommendation("Track 1", "Artist 1", "url1", "link1")
        )
        coEvery { musicRepository.getTopTracks(any(), any()) } returns Result.success(recommendations)
        
        assertFalse(viewModel.isLoading.value)
        viewModel.loadTopTracks()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }
    
    @Test
    fun `loadTopTracks limpia recomendaciones al iniciar`() = runTest {
        val recommendations = listOf(
            MusicRecommendation("Track 1", "Artist 1", "url1", "link1")
        )
        coEvery { musicRepository.getTopTracks(any(), any()) } returns Result.success(recommendations)
        
        viewModel.loadTopTracks()
        
        assertTrue(viewModel.recommendations.value.isEmpty())
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.recommendations.value.isEmpty())
    }
    
    @Test
    fun `loadTracksByTag establece isLoading correctamente`() = runTest {
        val recommendations = listOf(
            MusicRecommendation("Track 1", "Artist 1", "url1", "link1")
        )
        coEvery { musicRepository.getTracksByTag(any(), any(), any()) } returns Result.success(recommendations)
        
        assertFalse(viewModel.isLoading.value)
        viewModel.loadTracksByTag("rock")
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }
}


