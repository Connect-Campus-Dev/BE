package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.exception.EmailDuplicateException
import cc.connectcampus.connect_campus.domain.member.exception.EmailNotFoundException
import cc.connectcampus.connect_campus.domain.member.exception.InvalidCredentialsException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class AuthServiceTest @Autowired constructor(
    private val authService: AuthService,
) {
    private lateinit var signupRequest: SignupRequest
    private lateinit var member: Member

    @BeforeEach
    fun before() {
        member = Member.fixture()
        signupRequest = SignupRequest(
            member.nickname,
            member.email,
            member.password,
            member.enrollYear,
            member.gender,
        )
    }

    @Test
    @Transactional
    fun `새로운 멤버 가입 성공`() {
        val savedMember = authService.signup(signupRequest)
        assertThat(savedMember.nickname).isEqualTo(member.nickname)
        assertThat(savedMember.email).isEqualTo(member.email)
        assertThat(savedMember.password).isEqualTo(member.password)
        assertThat(savedMember.enrollYear).isEqualTo(member.enrollYear)
        assertThat(savedMember.gender).isEqualTo(member.gender)
    }

    @Test
    @Transactional
    fun `이메일 중복 오류 발생`() {
        authService.signup(signupRequest)
        assertThrows<EmailDuplicateException> {
            authService.signup(signupRequest)
        }.message?.let {
            assertThat(it).isEqualTo("이미 사용중인 이메일입니다.")
        }
    }

    @Test
    @Transactional
    fun `로그인 성공`() {
        val savedMember = authService.signup(signupRequest)
        val loginRequest = LoginRequest(savedMember.email, savedMember.password)
        val loginMember = authService.login(loginRequest)
        assertThat(loginMember).isEqualTo(savedMember)
    }

    @Test
    @Transactional
    fun `아이디 또는 비밀번호 오류`() {
        val savedMember = authService.signup(signupRequest)
        val loginRequest = LoginRequest(Email("wrongEmail@test.com"), savedMember.password)
        assertThrows<InvalidCredentialsException> {
            authService.login(loginRequest)
        }.message?.let {
            assertThat(it).isEqualTo("이메일 또는 비밀번호가 잘못되었습니다.")
        }

        val loginRequest2 = LoginRequest(savedMember.email, "wrongPassword")
        assertThrows<InvalidCredentialsException> {
            authService.login(loginRequest2)
        }.message?.let {
            assertThat(it).isEqualTo("이메일 또는 비밀번호가 잘못되었습니다.")
        }
    }
}