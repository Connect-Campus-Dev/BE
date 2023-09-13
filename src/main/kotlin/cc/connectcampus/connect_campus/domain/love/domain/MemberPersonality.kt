package cc.connectcampus.connect_campus.domain.love.domain

import jakarta.persistence.*
import java.util.*

@Entity
class MemberPersonality (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loveprofile_id")
    val loveProfile: LoveProfile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_id")
    val personality: Personality,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)