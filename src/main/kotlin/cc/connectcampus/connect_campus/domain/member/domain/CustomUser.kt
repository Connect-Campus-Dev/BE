package cc.connectcampus.connect_campus.domain.member.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*

class CustomUser(
    val id: UUID?,
    email: String,
    password: String,
    authorities: Collection<GrantedAuthority>,
    ): User(email, password, authorities)