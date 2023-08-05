package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.exception.EmailDuplicateException
import cc.connectcampus.connect_campus.domain.member.exception.EmailNotFoundException
import cc.connectcampus.connect_campus.domain.member.exception.InvalidCredentialsException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.sign

@Service
class AuthService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun signup(signupRequest: SignupRequest): Member {
        if(memberRepository.existsByEmail(signupRequest.email)) {
            throw EmailDuplicateException()
        }
        return memberRepository.save(
            Member(
                nickname = signupRequest.nickname,
                email = signupRequest.email,
                password = signupRequest.password,
                enrollYear = signupRequest.enrollYear,
                gender = signupRequest.gender,
            )
        )
    }

    @Transactional
    fun login(loginRequest: LoginRequest): Member {
        val member = memberRepository.findByEmail(loginRequest.email) ?: throw InvalidCredentialsException()
        if(member.password != loginRequest.password) {
            throw InvalidCredentialsException()
        }
        return member
    }
}