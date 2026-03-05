package com.example.location.domain.usecase

import com.example.location.domain.model.Room
import com.example.location.domain.repository.RoomRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(name: String): Result<Room> {
        return roomRepository.createRoom(name)
    }
}
