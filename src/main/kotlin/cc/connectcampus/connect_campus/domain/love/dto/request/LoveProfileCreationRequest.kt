package cc.connectcampus.connect_campus.domain.love.dto.request

import cc.connectcampus.connect_campus.domain.love.domain.LoveProfile
import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class LoveProfileCreationRequest (
    @field:NotBlank @field:Size(min = 2, max = 10)
    private val nickname: String,
    @field:NotBlank
    private val residence: String,
    @field:NotBlank
    private val mbti: String,
    @field:NotNull
    private val height: Int,
    private val introduction: String,
) {
    fun toLoveProfile(member: Member): LoveProfile {
        return LoveProfile(
            nickname = nickname,
            residence = residence,
            mbti = mbti,
            height = height,
            introduction = introduction,
            member = member,
        )
    }
}