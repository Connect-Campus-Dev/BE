package cc.connectcampus.connect_campus.domain.chat.domain

import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import java.util.*

@Entity
class Chat(
    val title: String,

    @Enumerated(EnumType.STRING)
    val type: ChatType,

    @OneToMany(mappedBy = "chat", cascade = [CascadeType.ALL])
    val members: MutableList<ChatMember>,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID ?= null,
) {
}