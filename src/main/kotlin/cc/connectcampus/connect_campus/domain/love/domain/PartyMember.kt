package cc.connectcampus.connect_campus.domain.love.domain

import jakarta.persistence.*
import java.util.*

@Entity
class PartyMember (
    val isOwner: Boolean,
    val isAccepted: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loveprofile_id")
    val loveProfile: LoveProfile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    val party: Party,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)