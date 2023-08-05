package cc.connectcampus.connect_campus.domain.member.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
data class ProfileImage (
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member,

    @Column(nullable = false)
    val imageUrl: String
)