package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MusicViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {
    
    private val _recommendations = MutableStateFlow<List<MusicRecommendation>>(emptyList())
    val recommendations: StateFlow<List<MusicRecommendation>> = _recommendations.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadTopTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Limpiar recomendaciones anteriores para mostrar que se está actualizando
            _recommendations.value = emptyList()
            
            // Generar una página aleatoria para obtener diferentes canciones cada vez
            val randomPage = Random.nextInt(1, 20)
            
            musicRepository.getTopTracks(limit = 10, page = randomPage).onSuccess { tracks ->
                // Tomar solo los primeros 5 para mostrar
                _recommendations.value = tracks.take(5)
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar recomendaciones"
                _isLoading.value = false
            }
        }
    }
    
    fun loadTracksByTag(tag: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // También podemos aleatorizar esto si se desea
            val randomPage = Random.nextInt(1, 20)
            
            musicRepository.getTracksByTag(tag, limit = 5, page = randomPage).onSuccess { tracks ->
                _recommendations.value = tracks
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar recomendaciones"
                _isLoading.value = false
            }
        }
    }
}
