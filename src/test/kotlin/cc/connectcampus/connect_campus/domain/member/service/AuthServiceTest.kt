package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.dto.request.CodeVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.EmailVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.LoginRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.SignupRequest
import cc.connectcampus.connect_campus.domain.member.exception.EmailDuplicateException
import cc.connectcampus.connect_campus.domain.member.exception.InvalidCredentialsException
import cc.connectcampus.connect_campus.domain.member.exception.NicknameDuplicateException
import cc.connectcampus.connect_campus.domain.member.repository.EmailVerificationRepository
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class AuthServiceTest @Autowired constructor(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val codeGenerator: CodeGenerator,
    private val emailVerificationRepository: EmailVerificationRepository
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

//    @Test
//    @Transactional
//    fun `새로운 멤버 가입 성공`() {
//        emailVerificationRepository.save(
//            EmailVerification(
//                email = signupRequest.email,
//                code = codeGenerator.generate(),
//                isVerified = true
//            )
//        )
//        val savedMember = authService.signup(signupRequest)
//        assertThat(savedMember.nickname).isEqualTo(member.nickname)
//        assertThat(savedMember.email).isEqualTo(member.email)
//        assertThat(savedMember.password).isEqualTo(member.password)
//        assertThat(savedMember.enrollYear).isEqualTo(member.enrollYear)
//        assertThat(savedMember.gender).isEqualTo(member.gender)
//    }

    @Test
    @Transactional
    fun `이메일 중복 오류 발생`() {
        val anotherRequest = SignupRequest(
            nickname = "another",
            email = member.email,
            password = "anotherPassword",
            enrollYear = 2023,
            gender = Gender.MALE,
        )
        emailVerificationRepository.saveAll(
            listOf(
                EmailVerification(
                    email = signupRequest.email,
                    code = codeGenerator.generate(),
                    isVerified = true
                ),
                EmailVerification(
                    email = anotherRequest.email,
                    code = codeGenerator.generate(),
                    isVerified = true
                )
            )
        )
        authService.signup(signupRequest)

        assertThrows<EmailDuplicateException> {
            authService.signup(anotherRequest)
        }.message?.let {
            assertThat(it).isEqualTo("이미 사용중인 이메일입니다.")
        }
    }

    @Test
    @Transactional
    fun `닉네임 중복 오류 발생`() {
        val anotherRequest = SignupRequest(
            nickname = member.nickname,
            email = Email("another@ajou.ac.kr"),
            password = "anotherPassword",
            enrollYear = 2023,
            gender = Gender.MALE,
        )
        emailVerificationRepository.saveAll(
            listOf(
                EmailVerification(
                    email = signupRequest.email,
                    code = codeGenerator.generate(),
                    isVerified = true
                ),
                EmailVerification(
                    email = anotherRequest.email,
                    code = codeGenerator.generate(),
                    isVerified = true
                )
            )
        )
        authService.signup(signupRequest)



        assertThrows<NicknameDuplicateException> {
            authService.signup(anotherRequest)
        }.message?.let {
            assertThat(it).isEqualTo("이미 사용중인 닉네임입니다.")
        }
    }

//    @Test
//    @Transactional
//    fun `로그인 성공`() {
//        emailVerificationRepository.save(
//            EmailVerification(
//                email = signupRequest.email,
//                code = codeGenerator.generate(),
//                isVerified = true
//            )
//        )
//        val savedMember = authService.signup(signupRequest)
//        val loginRequest = LoginRequest(savedMember.email, savedMember.password)
//        val loginMember = authService.login(loginRequest)
//        assertThat(loginMember).isEqualTo(savedMember)
//    }

//    @Test
//    @Transactional
//    fun `아이디 또는 비밀번호 오류`() {
//        emailVerificationRepository.save(
//            EmailVerification(
//                email = signupRequest.email,
//                code = codeGenerator.generate(),
//                isVerified = true
//            )
//        )
//        val savedMember = authService.signup(signupRequest)
//        val loginRequest = LoginRequest(Email("wrongEmail@test.com"), savedMember.password)
//        assertThrows<InvalidCredentialsException> {
//            authService.login(loginRequest)
//        }.message?.let {
//            assertThat(it).isEqualTo("이메일 또는 비밀번호가 잘못되었습니다.")
//        }
//
//        val loginRequest2 = LoginRequest(savedMember.email, "wrongPassword")
//        assertThrows<InvalidCredentialsException> {
//            authService.login(loginRequest2)
//        }.message?.let {
//            assertThat(it).isEqualTo("이메일 또는 비밀번호가 잘못되었습니다.")
//        }
//    }

    @Test
    @Transactional
    fun `이메일 인증 코드 발송`() {
        //Member class의 fixture 함수에서 본인 이메일로 변경 후, 테스트 코드 실행
        //본인 이메일로 코드 발송되는 지 확인
        val request = EmailVerificationRequest(signupRequest.email)
        val emailVerification = emailVerificationService.sendVerificationEmail(request)
        assertThat(emailVerification.email).isEqualTo(signupRequest.email)
        assertThat(emailVerification.code).isNotNull
    }

    @Test
    @Transactional
    fun `이메일 인증 시, 이미 가입된 회원`() {
        emailVerificationRepository.save(
            EmailVerification(
                email = signupRequest.email,
                code = codeGenerator.generate(),
                isVerified = true
            )
        )
        authService.signup(signupRequest)
        val request = EmailVerificationRequest(signupRequest.email)

        assertThrows<EmailDuplicateException> {
            emailVerificationService.sendVerificationEmail(request)
        }.message?.let {
            assertThat(it).isEqualTo("이미 사용중인 이메일입니다.")
        }
    }

    @Test
    @Transactional
    fun `올바른 인증 코드 입력`() {
        val code = codeGenerator.generate()
        val emailVerification = EmailVerification(signupRequest.email, code)
        emailVerificationRepository.save(emailVerification)

        val codeVerificationRequest = CodeVerificationRequest(code, emailVerification.id!!)

        //return 값이 "ok"인지 확인
        emailVerificationService.verifyCode(codeVerificationRequest).let {
            assertThat(it).isTrue()
        }
    }

    @Test
    fun `이메일 인증 타임 아웃`() {
        val code = codeGenerator.generate()
        val emailVerification = EmailVerification(
            email = signupRequest.email,
            code = code,
            createdAt = LocalDateTime.now().minusMinutes(4)
        )
        emailVerificationRepository.save(emailVerification)

        val codeVerificationRequest = CodeVerificationRequest(code, emailVerification.id!!)

        assertThrows<BusinessException> {
            emailVerificationService.verifyCode(codeVerificationRequest)
        }.message?.let {
            assertThat(it).isEqualTo("인증 시간이 초과되었습니다.")
        }
    }

    @Test
    fun `인증 코드 불일치`() {
        val code = codeGenerator.generate()
        var anotherCode: String
        do {
            anotherCode = codeGenerator.generate()
        } while(code == anotherCode)

        val emailVerification = EmailVerification(
            email = signupRequest.email,
            code = code,
        )
        emailVerificationRepository.save(emailVerification)

        val codeVerificationRequest = CodeVerificationRequest(anotherCode, emailVerification.id!!)

        assertThrows<InvalidValueException> {
            emailVerificationService.verifyCode(codeVerificationRequest)
        }
    }

    @Test
    fun `이메일 인증을 받지 않은 사용자가 회원가입을 시도`() {
        assertThrows<BusinessException> {
            authService.signup(signupRequest)
        }.message?.let {
            assertThat(it).isEqualTo("인증되지 않은 사용자입니다.")
        }
    }




}