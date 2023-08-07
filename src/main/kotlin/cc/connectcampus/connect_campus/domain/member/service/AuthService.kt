package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.CodeVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.EmailVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.dto.response.EmailVerificationResponse

interface AuthService {
    fun signup(signupRequest: SignupRequest): Member
    fun login(loginRequest: LoginRequest): Member
    fun verifyNickname(nickname: String): Boolean
}