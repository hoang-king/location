package com.example.location.data.repository

import com.example.location.data.remote.firebase.FirebaseAuthService
import com.example.location.data.remote.firestore.RoomService
import com.example.location.domain.repository.ChatRepository
import com.example.location.domain.repository.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val roomService: RoomService,
    private val authService: FirebaseAuthService
) : ChatRepository {

    override fun getMessages(roomId: String): Flow<List<Message>> {
        return roomService.getMessages(roomId).map { messages ->
            messages.map { it.toDomainMessage() }
        }
    }

    override suspend fun sendMessage(roomId: String, content: String): Result<Unit> {
        val user = authService.currentUser
            ?: return Result.failure(Exception("Chưa đăng nhập"))

        val messageData = hashMapOf(
            "roomId" to roomId,
            "senderId" to user.uid,
            "senderName" to (user.displayName ?: "Ẩn danh"),
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )
        return roomService.sendMessage(roomId, messageData)
    }
}
