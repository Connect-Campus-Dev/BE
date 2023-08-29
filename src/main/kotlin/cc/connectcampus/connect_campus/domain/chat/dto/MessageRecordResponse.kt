package cc.connectcampus.connect_campus.domain.chat.dto

import cc.connectcampus.connect_campus.domain.chat.domain.ChatType
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDateTime
import java.util.UUID

data class MessageRecordResponse(
    val chat: ChatInfo,
    val lastMessage: MessageInfo
)

data class ChatInfo(
    val id: UUID,
    val title: String,
    @Enumerated(EnumType.STRING)
    val type: ChatType,
    val memberCount: Int,
    val unreadMessageCount: Int
)

data class MessageInfo(
    val senderId: UUID?,
    val senderNickname: String?,
    val content: String,
    val sentAt: LocalDateTime
)
