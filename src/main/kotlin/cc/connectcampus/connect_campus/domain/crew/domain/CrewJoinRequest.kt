package cc.connectcampus.connect_campus.domain.crew.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class CrewJoinRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    val crew: Crew,

    @ManyToOne
    val member: Member,

    val requestTime: LocalDateTime = LocalDateTime.now(),
    )