package cc.connectcampus.connect_campus.domain.chat.repository

import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.domain.QChatMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class ChatQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun notExists(chatId: UUID, memberId: UUID): Boolean {
        val chatMember = QChatMember.chatMember
        return queryFactory.selectFrom(chatMember)
            .where(chatMember.chat.id.eq(chatId)
                .and(chatMember.member.id.eq(memberId)))
            .fetchFirst() == null
    }

    fun chats(memberId: UUID): List<ChatMember> {
        val chatMember = QChatMember.chatMember
        return queryFactory.selectFrom(chatMember)
            .where(chatMember.member.id.eq(memberId))
            .fetch()
    }
}