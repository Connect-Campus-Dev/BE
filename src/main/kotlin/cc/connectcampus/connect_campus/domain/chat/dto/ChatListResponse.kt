package cc.connectcampus.connect_campus.domain.chat.dto

import java.time.LocalDateTime

data class ChatListResponse(
    val id: Long,
    val title: String,
    val memberCount: Int,
    val chatProfileImage: String,
    val lastMessage: String,
    val lastMessageTime: LocalDateTime,
)