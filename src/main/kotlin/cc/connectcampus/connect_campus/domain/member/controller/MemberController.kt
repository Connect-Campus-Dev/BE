package cc.connectcampus.connect_campus.domain.member.controller

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.dto.response.MemberResponse
import cc.connectcampus.connect_campus.domain.member.service.MemberServiceImpl
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController
@Slf4j
class MemberController(
    private val memberServiceImpl: MemberServiceImpl,
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    @GetMapping("/member")
    fun getMemberInfo(): MemberResponse {
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id!!
        logger.info(userId.toString())
        val findMember = memberServiceImpl.getMemberInfo(userId)
        return MemberResponse.fromMember(findMember)
    }
}