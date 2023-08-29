package cc.connectcampus.connect_campus.domain.member.domain

import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import cc.connectcampus.connect_campus.domain.member.domain.Gender.*
import cc.connectcampus.connect_campus.domain.model.Email
import jakarta.persistence.*
import lombok.EqualsAndHashCode
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class Member(
    @Column(nullable = false, unique = true)
    val nickname: String,

    @Embedded
    @AttributeOverride(
        name = "value",
        column = Column(name = "email", nullable = false, unique = true)
    )
    val email: Email,

    val password: String,
    val enrollYear: Int,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "member", orphanRemoval = true)
    val profileImage: List<ProfileImage>? = null,

    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @CreationTimestamp
    @Column(name = "created_at",  updatable = false)
    val createdAt: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID ?= null,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
    val joinedCrew: MutableList<CrewMember> = mutableListOf(),
) {
    companion object {
        fun fixture(
            nickname: String = "TestMember",
            email: Email = Email("12181671@inha.edu"),
            password: String = "password123",
            enrollYear: Int = 2023,
            profileImage: List<ProfileImage>? = null,
            gender: Gender = MALE,
            createdAt: LocalDateTime = LocalDateTime.now(),
            id: UUID? = null,
        ): Member {
            return Member(nickname, email, password, enrollYear, profileImage, gender, createdAt, id)
        }
    }
}