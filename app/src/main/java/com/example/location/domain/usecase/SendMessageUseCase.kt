package com.example.location.domain.usecase

import com.example.location.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, content: String): Result<Unit> {
        return chatRepository.sendMessage(roomId, content)
    }
}
