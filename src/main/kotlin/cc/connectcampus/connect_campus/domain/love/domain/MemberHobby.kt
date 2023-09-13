package cc.connectcampus.connect_campus.domain.love.domain

import jakarta.persistence.*
import java.util.*

@Entity
class MemberHobby (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loveprofile_id")
    val loveProfile: LoveProfile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_id")
    val hobby: Hobby,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)