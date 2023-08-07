package cc.connectcampus.connect_campus.domain.member.dto.response

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.model.Email

data class SignupResponse(
    val nickname: String,
    val email: Email,
) {
    companion object {
        fun fromMember(member: Member): SignupResponse {
            return SignupResponse(
                nickname = member.nickname,
                email = member.email,
            )
        }
    }


}