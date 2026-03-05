package com.example.location.utils

object Constants {
    // Firebase Collections
    const val COLLECTION_ROOMS = "rooms"
    const val COLLECTION_MESSAGES = "messages"
    const val COLLECTION_LOCATIONS = "locations"
    const val COLLECTION_USERS = "users"

    // Navigation Routes
    const val ROUTE_LOGIN = "login"
    const val ROUTE_ROOMS = "rooms"
    const val ROUTE_MAP = "map/{roomId}"

    // Location
    const val LOCATION_UPDATE_INTERVAL = 5000L // 5 giây
    const val LOCATION_FASTEST_INTERVAL = 3000L // 3 giây

    // Room Code
    const val ROOM_CODE_LENGTH = 6

    // Notification
    const val LOCATION_NOTIFICATION_CHANNEL_ID = "location_tracking"
    const val LOCATION_NOTIFICATION_ID = 1
}
