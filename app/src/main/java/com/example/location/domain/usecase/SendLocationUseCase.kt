package com.example.location.domain.usecase

import com.example.location.domain.model.Location
import com.example.location.domain.repository.LocationRepository
import javax.inject.Inject

class SendLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(roomId: String, location: Location): Result<Unit> {
        return locationRepository.sendLocation(roomId, location)
    }
}
