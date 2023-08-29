package cc.connectcampus.connect_campus.domain.member.repository

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.model.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository: JpaRepository<Member, Long> {
    fun existsByEmail(email: Email): Boolean
    fun findByEmail(email: Email): Member?
    fun existsByNickname(nickname: String): Boolean
    fun findById(id: UUID): Member?
}