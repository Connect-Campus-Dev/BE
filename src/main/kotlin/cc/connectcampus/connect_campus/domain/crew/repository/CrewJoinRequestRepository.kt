package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.CrewJoinRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CrewJoinRequestRepository: JpaRepository<CrewJoinRequest, Long> {
    fun existsByCrewIdAndMemberId(crewId: UUID, memberId: UUID): Boolean

    fun findAllByCrewIdOrderByRequestTime(crewId: UUID): List<CrewJoinRequest>

    fun findById(crewJoinRequestId: UUID): CrewJoinRequest?
}