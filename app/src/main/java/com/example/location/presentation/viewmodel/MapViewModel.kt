package com.example.location.presentation.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.domain.model.Location
import com.example.location.domain.repository.LocationRepository
import com.example.location.domain.usecase.SendLocationUseCase
import com.example.location.service.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val locations: List<Location> = emptyList(),
    val myLatitude: Double = 0.0,
    val myLongitude: Double = 0.0,
    val currentUserId: String = "",
    val isTracking: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val sendLocationUseCase: SendLocationUseCase,
    private val authService: FirebaseAuthService,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val roomId: String = savedStateHandle.get<String>("roomId") ?: ""

    private val _uiState = MutableStateFlow(MapUiState(currentUserId = authService.currentUserId))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        if (roomId.isNotEmpty()) {
            observeLocations()
        }
    }

    private fun observeLocations() {
        viewModelScope.launch {
            locationRepository.getLocationsInRoom(roomId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { locations ->
                    _uiState.value = _uiState.value.copy(locations = locations)
                }
        }
    }

    fun startTracking() {
        _uiState.value = _uiState.value.copy(isTracking = true)

        LocationService.onLocationUpdate = { lat, lng, bearing ->
            _uiState.value = _uiState.value.copy(
                myLatitude = lat,
                myLongitude = lng
            )
            sendMyLocation(lat, lng, bearing)
        }

        val intent = Intent(application, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        application.startService(intent)
    }

    fun stopTracking() {
        _uiState.value = _uiState.value.copy(isTracking = false)

        val intent = Intent(application, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        application.startService(intent)

        // Xóa vị trí khỏi room
        viewModelScope.launch {
            locationRepository.removeLocation(roomId, authService.currentUserId)
        }
    }

    private fun sendMyLocation(lat: Double, lng: Double, bearing: Float) {
        viewModelScope.launch {
            val location = Location(
                userId = authService.currentUserId,
                latitude = lat,
                longitude = lng,
                bearing = bearing,
                timestamp = System.currentTimeMillis(),
                displayName = authService.currentUser?.displayName ?: "Ẩn danh"
            )
            sendLocationUseCase(roomId, location)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    override fun onCleared() {
        super.onCleared()
        if (_uiState.value.isTracking) {
            stopTracking()
        }
    }
}
