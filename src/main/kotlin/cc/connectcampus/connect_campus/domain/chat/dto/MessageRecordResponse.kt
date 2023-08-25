package cc.connectcampus.connect_campus.domain.chat.dto

import java.time.LocalDateTime
import java.util.UUID

data class MessageRecordResponse(
    val chat: ChatInfo,
    val lastMessage: MessageInfo
)

data class ChatInfo(
    val id: UUID,
    val title: String,
    val type: String,
    val memberCount: Long,
    val unreadMessageCount: Long
)

data class MessageInfo(
    val senderId: UUID?,
    val senderNickname: String?,
    val content: String,
    val sentAt: LocalDateTime
)
