package cc.connectcampus.connect_campus.domain.member.dto.response

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.ProfileImage
import cc.connectcampus.connect_campus.domain.model.Email
import java.util.UUID

data class LoginResponse(
    val nickname: String,
    val email: Email,
    val enrollYear: Int,
    val profileImage: List<ProfileImage>?,
    val gender: Gender,
    val id: UUID?,
) {
    companion object {
        fun fromMember(member: Member): LoginResponse {
            return LoginResponse(
                nickname = member.nickname,
                email = member.email,
                enrollYear = member.enrollYear,
                profileImage = member.profileImage,
                gender = member.gender,
                id = member.id,
            )
        }
    }
}