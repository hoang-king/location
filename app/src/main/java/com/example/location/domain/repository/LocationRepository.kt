package com.example.location.domain.repository

import com.example.location.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationsInRoom(roomId: String): Flow<List<Location>>
    suspend fun sendLocation(roomId: String, location: Location): Result<Unit>
    suspend fun removeLocation(roomId: String, userId: String): Result<Unit>
}
