package cc.connectcampus.connect_campus.domain.chat.dto

import java.time.LocalDateTime
import java.util.*

data class ChatListResponse(
    val id: UUID,
    val title: String,
    val memberCount: Int,
    val chatProfileImage: String,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime,
)