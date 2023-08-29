package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
) {
    fun getMemberInfo(userId: UUID): Member {
        return memberRepository.findById(userId) ?: throw MemberNotFoundException()
    }


}