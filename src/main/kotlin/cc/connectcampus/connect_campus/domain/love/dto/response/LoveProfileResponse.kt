package cc.connectcampus.connect_campus.domain.love.dto.response

import cc.connectcampus.connect_campus.domain.love.domain.Hobby
import cc.connectcampus.connect_campus.domain.love.domain.LoveProfileImage
import cc.connectcampus.connect_campus.domain.love.domain.Personality

data class LoveProfileResponse (
    val nickname: String,
    val residence: String,
    val mbti: String,
    val height: Int,
    val introduction: String,
    val profileImage: List<LoveProfileImage>,
    val univ: String,
    val personality: List<String>,
    val hobby: List<String>,
)