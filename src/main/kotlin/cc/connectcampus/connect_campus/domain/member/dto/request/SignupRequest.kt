package cc.connectcampus.connect_campus.domain.member.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.Role
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.domain.univ.domain.Univ
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
    val enrollYear: Int,
    val gender: Gender,
) {
    fun toMember(): Member {
        return Member(
            nickname = this.nickname,
            email = this.email,
            password = this.password,
            enrollYear = this.enrollYear,
            gender = this.gender,
            role = Role.MEMBER,
        )
    }
}
