package com.example.location.data.repository

import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.data.remote.firestore.RoomService
import com.example.location.domain.model.Room
import com.example.location.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val roomService: RoomService,
    private val authService: FirebaseAuthService
) : RoomRepository {

    override suspend fun createRoom(name: String): Result<Room> {
        val userId = authService.currentUserId
        if (userId.isEmpty()) return Result.failure(Exception("Chưa đăng nhập"))

        val code = generateRoomCode()
        return roomService.createRoom(name, userId, code).map { it.toDomainModel() }
    }

    override suspend fun joinRoom(code: String): Result<Room> {
        val userId = authService.currentUserId
        if (userId.isEmpty()) return Result.failure(Exception("Chưa đăng nhập"))

        val normalizedCode = code.trim().uppercase()
        return roomService.joinRoom(normalizedCode, userId).map { it.toDomainModel() }
    }

    override suspend fun leaveRoom(roomId: String): Result<Unit> {
        val userId = authService.currentUserId
        if (userId.isEmpty()) return Result.failure(Exception("Chưa đăng nhập"))

        return roomService.leaveRoom(roomId, userId)
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> {
        val userId = authService.currentUserId
        if (userId.isEmpty()) return Result.failure(Exception("Chưa đăng nhập"))

        return roomService.deleteRoom(roomId)
    }

    override fun getRoomById(roomId: String): Flow<Room?> {
        return roomService.getRoomById(roomId).map { it?.toDomainModel() }
    }

    override fun getMyRooms(): Flow<List<Room>> {
        val userId = authService.currentUserId
        return roomService.getRoomsByUser(userId).map { rooms ->
            rooms.map { it.toDomainModel() }
        }
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
