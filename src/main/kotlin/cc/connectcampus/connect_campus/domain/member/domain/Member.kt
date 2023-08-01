package cc.connectcampus.connect_campus.domain.member.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?=null,
    val name: String
)