package cc.connectcampus.connect_campus.domain.member.dto.request

import cc.connectcampus.connect_campus.domain.model.Email
import jakarta.validation.Valid


data class EmailVerificationRequest(
    @field:Valid
    val email: Email,
)