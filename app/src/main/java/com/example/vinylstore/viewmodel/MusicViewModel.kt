package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            
            musicRepository.getTopTracks(limit = 5).onSuccess { tracks ->
                _recommendations.value = tracks
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
            
            musicRepository.getTracksByTag(tag, limit = 5).onSuccess { tracks ->
                _recommendations.value = tracks
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar recomendaciones"
                _isLoading.value = false
            }
        }
    }
}

