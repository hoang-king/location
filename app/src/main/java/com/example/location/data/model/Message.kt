package com.example.location.data.model

data class Message(
    val id: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Long = 0L
) {
    fun toDomainMessage() = com.example.location.domain.repository.Message(
        id = id,
        roomId = roomId,
        senderId = senderId,
        senderName = senderName,
        content = content,
        timestamp = timestamp
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Message {
            return Message(
                id = id,
                roomId = map["roomId"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                senderName = map["senderName"] as? String ?: "",
                content = map["content"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: 0L
            )
        }
    }
}
