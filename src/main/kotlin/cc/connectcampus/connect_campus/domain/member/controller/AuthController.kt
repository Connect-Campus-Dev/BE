package cc.connectcampus.connect_campus.domain.member.controller

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.CodeVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.EmailVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.dto.response.CodeVerificationResponse
import cc.connectcampus.connect_campus.domain.member.dto.response.EmailVerificationResponse
import cc.connectcampus.connect_campus.domain.member.dto.response.LoginResponse
import cc.connectcampus.connect_campus.domain.member.dto.response.SignupResponse
import cc.connectcampus.connect_campus.domain.member.service.AuthService
import cc.connectcampus.connect_campus.domain.member.service.EmailVerificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
) {
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody signupRequest: SignupRequest): SignupResponse {
        val member: Member = authService.signup(signupRequest)
        return SignupResponse.fromMember(member)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): LoginResponse {
        val member: Member = authService.login(loginRequest)
        return LoginResponse.fromMember(member)
    }

    @PostMapping("/nickname")
    fun verifyNickname(
        @RequestBody nickname: String
    ): Boolean {
        return authService.verifyNickname(nickname)
    }

    //이메일 요청 및 재요청처리
    @PostMapping("/email/request")
    fun sendVerificationEmail(
        @Valid @RequestBody emailVerificationRequest: EmailVerificationRequest
    ): EmailVerificationResponse {
        val emailVerification: EmailVerification = emailVerificationService.sendVerificationEmail(emailVerificationRequest)
        return EmailVerificationResponse.fromEmailVerification(emailVerification)
    }

    @PostMapping("/email/verify")
    fun verifyEmail(
        @Valid @RequestBody codeVerificationRequest: CodeVerificationRequest
    ): CodeVerificationResponse {
        return CodeVerificationResponse(
            isVerified = emailVerificationService.verifyCode(codeVerificationRequest)
        )
    }

}