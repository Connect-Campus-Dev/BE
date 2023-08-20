package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.Role
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.exception.*
import cc.connectcampus.connect_campus.domain.member.repository.EmailVerificationQuerydslRepository
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
import cc.connectcampus.connect_campus.global.config.security.JwtTokenProvider
import cc.connectcampus.connect_campus.global.config.security.TokenInfo
import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import lombok.extern.slf4j.Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class AuthServiceV0(
    private val memberRepository: MemberRepository,
    private val emailVerificationQuerydslRepository: EmailVerificationQuerydslRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
): AuthService {

    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun signup(signupRequest: SignupRequest): Member {
        val member = signupRequest.toMember()
        member.password = passwordEncoder.encode(member.password)
        verifySignupOrThrowException(member)
        return memberRepository.save(member)
    }

    @Transactional
    override fun login(loginRequest: LoginRequest): TokenInfo {
        //인증 토큰 생성
        val authenticationToken = UsernamePasswordAuthenticationToken(loginRequest.email.value, loginRequest.password)
        //AuthenticationManager는 DaoAuthenticationProvider를 통해 인증을 진행한다.
        //DaoAuthenticationProvider는 UserDetailsService를 통해 유저 정보를 검증하고, Authentication을 반환한다.
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return jwtTokenProvider.createToken(authentication)
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