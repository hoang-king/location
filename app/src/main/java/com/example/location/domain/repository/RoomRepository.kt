package com.example.location.domain.repository

import com.example.location.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun createRoom(name: String): Result<Room>
    suspend fun joinRoom(code: String): Result<Room>
    suspend fun leaveRoom(roomId: String): Result<Unit>
    suspend fun deleteRoom(roomId: String): Result<Unit>
    fun getRoomById(roomId: String): Flow<Room?>
    fun getMyRooms(): Flow<List<Room>>
}
