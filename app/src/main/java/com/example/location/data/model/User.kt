package com.example.location.data.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L
) {
    fun toDomainModel() = com.example.location.domain.model.User(
        uid = uid,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl,
        isOnline = isOnline
    )

    companion object {
        fun fromDomainModel(user: com.example.location.domain.model.User) = User(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl,
            isOnline = user.isOnline
        )
    }
}
