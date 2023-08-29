package cc.connectcampus.connect_campus.domain.chat.dto

import java.time.LocalDateTime
import java.util.*

data class MessageResponse(
    val chatId: UUID,
    val senderId: UUID,
    val senderName: String,
    val message: String,
    val sentAt: LocalDateTime,
)