package cc.connectcampus.connect_campus.domain.chat.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
class ChatMember(
    @ManyToOne
    @JoinColumn(name = "chat_id")
    val chat: Chat,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @CreationTimestamp
    val joinedAt: LocalDateTime ?= null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID ?= null,
) {

}