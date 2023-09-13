package cc.connectcampus.connect_campus.domain.love.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import java.util.*

@Entity
class LoveProfile(
    val nickname: String,
    val residence: String,
    val mbti: String,
    val height: Int,
    val introduction: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)