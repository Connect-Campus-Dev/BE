package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Slf4j
@Service
class UserDetailServiceImpl(
    private val memberRepository: MemberRepository,
): UserDetailsService {

    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    override fun loadUserByUsername(email: String): UserDetails {
        return memberRepository.findByEmail(Email(email))
            ?. let { createUserDetails(it) }
            ?: throw UsernameNotFoundException(ErrorCode.LOGIN_INPUT_INVALID.message)
    }

    private fun createUserDetails(member: Member): UserDetails {
        return CustomUser(
            id = member.id,
            nickname = member.nickname,
            email = member.email.value,
            password = member.password,
            //한 유저는 단일 권한을 가진다. 만약 여러 권한을 가질 경우 map으로 변환하여 처리
            authorities = listOf(SimpleGrantedAuthority("ROLE_${member.role.name}"))
        )
    }
}