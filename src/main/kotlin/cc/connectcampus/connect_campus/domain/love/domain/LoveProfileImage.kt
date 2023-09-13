package cc.connectcampus.connect_campus.domain.love.domain

import jakarta.persistence.*
import java.util.*

@Entity
data class LoveProfileImage(
    val isRepresentative: Boolean,
    val imageUrl: String,
    val fileName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loveprofile_id")
    val loveProfile: LoveProfile,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,
)