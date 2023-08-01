package cc.connectcampus.connect_campus.domain.member.repository

import cc.connectcampus.connect_campus.domain.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface memberRepository: JpaRepository<Member,Long> {
}