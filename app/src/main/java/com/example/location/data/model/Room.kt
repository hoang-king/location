package com.example.location.data.model

data class Room(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val creatorId: String = "",
    val members: List<String> = emptyList(),
    val createdAt: Long = 0L
) {
    fun toDomainModel() = com.example.location.domain.model.Room(
        id = id,
        name = name,
        code = code,
        creatorId = creatorId,
        members = members,
        createdAt = createdAt
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Room {
            return Room(
                id = id,
                name = map["name"] as? String ?: "",
                code = map["code"] as? String ?: "",
                creatorId = map["creatorId"] as? String ?: "",
                members = (map["members"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                createdAt = map["createdAt"] as? Long ?: 0L
            )
        }
    }
}
