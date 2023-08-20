package cc.connectcampus.connect_campus.domain.member.dto.response

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.model.Email

data class MemberResponse(
    val nickname: String,
    val email: Email,
    val enrollYear: Int,
) {
    companion object {
        fun fromMember(member: Member): MemberResponse {
            return MemberResponse(
                nickname = member.nickname,
                email = member.email,
                enrollYear = member.enrollYear,
            )
        }
    }

}