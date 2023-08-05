package cc.connectcampus.connect_campus.domain.member.controller

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup")
    fun signup(@RequestBody signupRequest: SignupRequest): Member {
        return authService.signup(signupRequest)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): Member {
        println("loginRequest hello")
        return authService.login(loginRequest)
    }
}