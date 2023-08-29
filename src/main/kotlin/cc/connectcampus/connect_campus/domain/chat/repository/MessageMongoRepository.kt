package cc.connectcampus.connect_campus.domain.chat.repository

import cc.connectcampus.connect_campus.domain.chat.domain.Message
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


interface MessageMongoRepository: MongoRepository<Message, String> {
    fun findFirstByChatIdAndSentAtGreaterThanOrderBySentAtDesc(chatId: UUID, joinedAt: LocalDateTime): Message?
    fun countByChatIdAndSentAtGreaterThanAndUnReadMembersNotContains(chatId: UUID, joinedAt: LocalDateTime, memberId: UUID): Int
}