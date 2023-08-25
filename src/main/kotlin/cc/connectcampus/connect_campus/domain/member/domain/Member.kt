package cc.connectcampus.connect_campus.domain.member.domain

import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import cc.connectcampus.connect_campus.domain.member.domain.Gender.*
import cc.connectcampus.connect_campus.domain.model.Email
import jakarta.persistence.*
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

    var password: String,
    val enrollYear: Int,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "member", orphanRemoval = true)
    val profileImage: List<ProfileImage>? = null,

    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @CreationTimestamp
    @Column(name = "created_at",  updatable = false)
    val createdAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
    val joinedCrew: MutableList<CrewMember> = mutableListOf(),

//    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL])
//    val joinedChat: MutableList<ChatMember> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    val role: Role,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID ?= null,

    ) {
    companion object {
        fun fixture(
            nickname: String = "TestMember",
            email: Email = Email("moonman0429@ajou.ac.kr"),
            password: String = "password123",
            enrollYear: Int = 2023,
            profileImage: List<ProfileImage>? = null,
            gender: Gender = MALE,
            createdAt: LocalDateTime = LocalDateTime.now(),
            joinedCrew: MutableList<CrewMember> = mutableListOf(),
            joinedChat: MutableList<ChatMember> = mutableListOf(),
            role: Role = Role.MEMBER,
            id: UUID? = null,
        ): Member {
            return Member(nickname, email, password, enrollYear, profileImage, gender, createdAt, joinedCrew, role, id)
        }
    }
}