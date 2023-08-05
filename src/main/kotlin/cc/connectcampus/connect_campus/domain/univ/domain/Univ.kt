package cc.connectcampus.connect_campus.domain.univ.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Univ(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "email_domain", nullable = false)
    var emailDomain: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)