package cc.connectcampus.connect_campus.domain.member.dto.response

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import cc.connectcampus.connect_campus.domain.model.Email
import java.util.*

data class EmailVerificationResponse(
    val email: Email,
    val id: UUID?,
) {
    companion object {
        fun fromEmailVerification(emailVerification: EmailVerification): EmailVerificationResponse {
            return EmailVerificationResponse(
                email = emailVerification.email,
                id = emailVerification.id,
            )
        }
    }
}