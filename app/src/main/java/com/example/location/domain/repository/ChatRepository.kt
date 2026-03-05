package com.example.location.domain.repository

import com.example.location.domain.model.User
import kotlinx.coroutines.flow.Flow

data class Message(
    val id: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

interface ChatRepository {
    fun getMessages(roomId: String): Flow<List<Message>>
    suspend fun sendMessage(roomId: String, content: String): Result<Unit>
}
