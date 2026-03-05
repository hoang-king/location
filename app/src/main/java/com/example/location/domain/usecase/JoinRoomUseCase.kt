package com.example.location.domain.usecase

import com.example.location.domain.model.Room
import com.example.location.domain.repository.RoomRepository
import javax.inject.Inject

class JoinRoomUseCase @Inject constructor(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(code: String): Result<Room> {
        return roomRepository.joinRoom(code)
    }
}
