package cc.connectcampus.connect_campus.domain.member.repository

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmailVerificationRepository: JpaRepository<EmailVerification, Long> {
    fun findById(id: UUID): EmailVerification?
}