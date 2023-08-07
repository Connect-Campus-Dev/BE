package cc.connectcampus.connect_campus.domain.member.domain

import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class EmailVerification (
    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "email", nullable = false, unique = false)
    )
    val email: Email,
    val code: String,
    var isVerified: Boolean = false,

//    @CreationTimestamp
    @Column(name = "created_at",  updatable = false)
    val createdAt: LocalDateTime? = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
) {
    fun isExpired(): Boolean {
        return createdAt?.let { it.plusMinutes(3) < LocalDateTime.now() }
            ?: throw BusinessException()
    }
}