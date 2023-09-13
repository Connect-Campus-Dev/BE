package cc.connectcampus.connect_campus.domain.love.service

import cc.connectcampus.connect_campus.domain.love.domain.Hobby
import cc.connectcampus.connect_campus.domain.love.domain.LoveProfileImage
import cc.connectcampus.connect_campus.domain.love.domain.MemberHobby
import cc.connectcampus.connect_campus.domain.love.domain.MemberPersonality
import cc.connectcampus.connect_campus.domain.love.dto.request.LoveProfileCreationRequest
import cc.connectcampus.connect_campus.domain.love.dto.response.LoveProfileResponse
import cc.connectcampus.connect_campus.domain.love.exception.LoveProfileNotFoundException
import cc.connectcampus.connect_campus.domain.love.repository.LoveProfileImageRepository
import cc.connectcampus.connect_campus.domain.love.repository.LoveProfileRepository
import cc.connectcampus.connect_campus.domain.love.repository.MemberHobbyRepository
import cc.connectcampus.connect_campus.domain.love.repository.MemberPersonalityRepository
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.global.CommonResponse
import cc.connectcampus.connect_campus.global.config.security.InvalidTokenException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class LoveProfileService(
    private val loveProfileRepository: LoveProfileRepository,
    private val memberRepository: MemberRepository,
    private val univRepository: UnivRepository,
    private val loveProfileImageRepository: LoveProfileImageRepository,
    private val memberPersonalityRepository: MemberPersonalityRepository,
    private val memberHobbyRepository: MemberHobbyRepository,
) {
    @Transactional
    fun createLoveProfile(
        loveProfileCreationRequest: LoveProfileCreationRequest
    ): CommonResponse {
        val memberId: UUID = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id
            ?: throw InvalidTokenException()

        val member = memberRepository.findById(memberId)

        val newLoveProfile = loveProfileCreationRequest.toLoveProfile(member!!)
        loveProfileRepository.save(newLoveProfile)

        return CommonResponse(message = "프로필 생성 성공")
    }

    @Transactional
    fun getLoveProfile(id: String): LoveProfileResponse {
        val memberId: UUID = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id
            ?: throw InvalidTokenException()

        //todo: 쿼리 최적화 필요
        val member = memberRepository.findById(memberId)
        val univ = univRepository.findByEmailDomain(member!!.email.getDomain())
            ?: throw Exception("존재하지 않는 대학교입니다.")
        val loveProfile = loveProfileRepository.findByMemberId(memberId)
            ?: throw LoveProfileNotFoundException()
        val personality: List<String> =
            memberPersonalityRepository.findByLoveProfileId(loveProfile.id!!)
                .map { it.personality.name }
        val hobby: List<String> = memberHobbyRepository.findByLoveProfileId(loveProfile.id)
            .map { it.hobby.name }
        val loveProfileImage: List<LoveProfileImage> = loveProfileImageRepository.findByLoveProfileId(loveProfile.id)

        return LoveProfileResponse(
            nickname = loveProfile.nickname,
            residence = loveProfile.residence,
            mbti = loveProfile.mbti,
            height = loveProfile.height,
            introduction = loveProfile.introduction,
            profileImage = loveProfileImage,
            univ = univ.name,
            personality = personality,
            hobby = hobby,
        )
    }
}