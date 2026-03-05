package com.example.location.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.domain.model.Room
import com.example.location.domain.usecase.CreateRoomUseCase
import com.example.location.domain.usecase.JoinRoomUseCase
import com.example.location.domain.usecase.DeleteRoomUseCase
import com.example.location.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomUiState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val userId: String = "",
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false
)

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val roomRepository: RoomRepository,
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val TAG = "RoomViewModel"
    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
        loadRooms()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = authService.isLoggedIn
        val userName = authService.currentUser?.displayName ?: ""
        val userId = authService.currentUserId
        Log.d(TAG, "checkLoginStatus: isLoggedIn=$isLoggedIn, userId=$userId")
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            userName = userName,
            userId = userId
        )
    }

    fun loadRooms() {
        viewModelScope.launch {
            Log.d(TAG, "loadRooms: Fetching rooms...")
            _uiState.value = _uiState.value.copy(isLoading = true)
            roomRepository.getMyRooms()
                .catch { e ->
                    Log.e(TAG, "loadRooms: Error fetching rooms", e)
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
                .collect { rooms ->
                    Log.d(TAG, "loadRooms: Success, found ${rooms.size} rooms")
                    _uiState.value = _uiState.value.copy(
                        rooms = rooms,
                        isLoading = false
                    )
                }
        }
    }

    fun createRoom(name: String) {
        viewModelScope.launch {
            Log.d(TAG, "createRoom: name=$name")
            _uiState.value = _uiState.value.copy(isLoading = true, showCreateDialog = false)
            createRoomUseCase(name).fold(
                onSuccess = { 
                    Log.d(TAG, "createRoom: Success")
                    loadRooms() 
                },
                onFailure = { e ->
                    Log.e(TAG, "createRoom: Error", e)
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun joinRoom(code: String) {
        viewModelScope.launch {
            Log.d(TAG, "joinRoom: code='$code'")
            _uiState.value = _uiState.value.copy(isLoading = true, showJoinDialog = false)
            joinRoomUseCase(code).fold(
                onSuccess = { 
                    Log.d(TAG, "joinRoom: Success")
                    loadRooms() 
                },
                onFailure = { e ->
                    Log.e(TAG, "joinRoom: Error", e)
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            Log.d(TAG, "deleteRoom: roomId=$roomId")
            _uiState.value = _uiState.value.copy(isLoading = true)
            deleteRoomUseCase(roomId).fold(
                onSuccess = { 
                    Log.d(TAG, "deleteRoom: Success")
                    loadRooms() 
                },
                onFailure = { e ->
                    Log.e(TAG, "deleteRoom: Error", e)
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun showCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        _uiState.value = _uiState.value.copy(showCreateDialog = false)
    }

    fun showJoinDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = true)
    }

    fun hideJoinDialog() {
        _uiState.value = _uiState.value.copy(showJoinDialog = false)
    }

    fun signOut() {
        Log.d(TAG, "signOut")
        authService.signOut()
        _uiState.value = RoomUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
