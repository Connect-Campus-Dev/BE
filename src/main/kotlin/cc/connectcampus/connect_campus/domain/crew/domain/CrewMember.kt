package cc.connectcampus.connect_campus.domain.crew.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
class CrewMember(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "crew_id")
    val crew: Crew,

    @ManyToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    @CreationTimestamp
    val joinedAt: LocalDateTime?= null,
)