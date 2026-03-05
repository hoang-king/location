package com.example.location.data.model

data class UserLocation(
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bearing: Float = 0f,
    val timestamp: Long = 0L,
    val displayName: String = ""
) {
    fun toDomainModel() = com.example.location.domain.model.Location(
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        bearing = bearing,
        timestamp = timestamp,
        displayName = displayName
    )

    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "latitude" to latitude,
        "longitude" to longitude,
        "bearing" to bearing,
        "timestamp" to timestamp,
        "displayName" to displayName
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): UserLocation {
            return UserLocation(
                userId = map["userId"] as? String ?: "",
                latitude = (map["latitude"] as? Number)?.toDouble() ?: 0.0,
                longitude = (map["longitude"] as? Number)?.toDouble() ?: 0.0,
                bearing = (map["bearing"] as? Number)?.toFloat() ?: 0f,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L,
                displayName = map["displayName"] as? String ?: ""
            )
        }

        fun fromDomainModel(location: com.example.location.domain.model.Location) = UserLocation(
            userId = location.userId,
            latitude = location.latitude,
            longitude = location.longitude,
            bearing = location.bearing,
            timestamp = location.timestamp,
            displayName = location.displayName
        )
    }
}
