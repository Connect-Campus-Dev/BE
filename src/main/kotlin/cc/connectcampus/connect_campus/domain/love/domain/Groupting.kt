package cc.connectcampus.connect_campus.domain.love.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Groupting (
    val signalMessage: String,

    @Enumerated(EnumType.STRING)
    val status: LoveStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_party_id")
    val party: Party,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_party_id")
    val toParty: Party,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)