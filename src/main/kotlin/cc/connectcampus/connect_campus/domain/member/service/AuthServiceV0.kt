package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.exception.*
import cc.connectcampus.connect_campus.domain.member.repository.EmailVerificationQuerydslRepository
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceV0(
    private val memberRepository: MemberRepository,
    private val emailVerificationQuerydslRepository: EmailVerificationQuerydslRepository,
): AuthService {

    @Transactional
    override fun signup(signupRequest: SignupRequest): Member {
        val member = signupRequest.toMember()
        verifySignupOrThrowException(member)

        return memberRepository.save(member)
    }

    @Transactional
    override fun login(loginRequest: LoginRequest): Member {
        val member = memberRepository.findByEmail(loginRequest.email) ?: throw InvalidCredentialsException()
        if(member.password != loginRequest.password) {
            throw InvalidCredentialsException()
        }
        return member
    }

    @Transactional
    override fun verifyNickname(nickname: String): Boolean {
        verifyNicknameOrThrowException(nickname)
        return true;
    }

    private fun verifySignupOrThrowException(member: Member) {
        if (emailVerificationQuerydslRepository.notExists(member.email)) {
            throw BusinessException(ErrorCode.NOT_VERIFIED)
        }

        verifyNicknameOrThrowException(member.nickname)

        if (memberRepository.existsByNickname(member.nickname)) {
            throw NicknameDuplicateException()
        }
        if (memberRepository.existsByEmail(member.email)) {
            throw EmailDuplicateException()
        }
    }

    private fun verifyNicknameOrThrowException(nickname: String) {
        if (nickname.length < 2 || nickname.length > 10) {
            throw NicknameLengthException()
        }
        if (InputFilter.isInputNotValid(nickname)) {
            throw InappropriateNicknameException()
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw NicknameDuplicateException()
        }
    }
}