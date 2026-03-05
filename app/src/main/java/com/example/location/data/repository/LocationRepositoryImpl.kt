package com.example.location.data.repository

import com.example.location.data.model.UserLocation
import com.example.location.data.remote.firestore.FirestoreService
import com.example.location.domain.model.Location
import com.example.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : LocationRepository {

    override fun getLocationsInRoom(roomId: String): Flow<List<Location>> {
        return firestoreService.getLocationsInRoom(roomId).map { locations ->
            locations.map { it.toDomainModel() }
        }
    }

    override suspend fun sendLocation(roomId: String, location: Location): Result<Unit> {
        val userLocation = UserLocation.fromDomainModel(location)
        return firestoreService.updateLocation(roomId, userLocation)
    }

    override suspend fun removeLocation(roomId: String, userId: String): Result<Unit> {
        return firestoreService.removeLocation(roomId, userId)
    }
}
