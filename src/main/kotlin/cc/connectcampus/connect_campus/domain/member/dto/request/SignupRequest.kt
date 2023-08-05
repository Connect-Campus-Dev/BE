package cc.connectcampus.connect_campus.domain.member.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.model.Email
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:NotBlank @field:Size(min = 2, max = 10)
    val nickname: String,
    @field:Valid
    val email: Email,
    @field:NotBlank @field:Size(min = 8)
    val password: String,
    @field:NotBlank
    val enrollYear: Int,
    @field:NotBlank
    val gender: Gender,
)
