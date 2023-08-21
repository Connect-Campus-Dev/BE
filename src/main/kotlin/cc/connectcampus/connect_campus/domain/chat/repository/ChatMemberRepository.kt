package cc.connectcampus.connect_campus.domain.chat.repository

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ChatMemberRepository: JpaRepository<ChatMember, UUID> {
    fun findAllByMember(member: Member): List<ChatMember>?
}