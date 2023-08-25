package cc.connectcampus.connect_campus.domain.chat.domain

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("Message")
class Message(
    val chatId: UUID? = null,
    val senderId: UUID? = null,
    val senderNickname: String? = null,
    val content: String? = null,
    var unReadMembers: List<UUID?> = mutableListOf(),
    @Enumerated(EnumType.STRING)
    val type: MessageType = MessageType.USER,
    @CreatedDate
    var sentAt: LocalDateTime? = null,
)