package cc.connectcampus.connect_campus.domain.chat.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
class ChatMember(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    @JsonBackReference
    val chat: Chat,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    val member: Member,

    @CreationTimestamp
    val joinedAt: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
) {

}