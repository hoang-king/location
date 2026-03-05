package com.example.location.domain.usecase

import com.example.location.domain.repository.RoomRepository
import javax.inject.Inject

class DeleteRoomUseCase @Inject constructor(
    private val repository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): Result<Unit> {
        return repository.deleteRoom(roomId)
    }
}
