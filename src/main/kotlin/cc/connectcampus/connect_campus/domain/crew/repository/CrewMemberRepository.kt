package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CrewMemberRepository: JpaRepository<CrewMember,Long> {
    fun existsByCrewIdAndMemberId(crewId: UUID, memberId: UUID): Boolean
}