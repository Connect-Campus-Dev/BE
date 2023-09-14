package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.domain.CrewJoinRequest
import cc.connectcampus.connect_campus.domain.crew.exception.CrewJoinRequestNotFoundException
import cc.connectcampus.connect_campus.domain.crew.exception.CrewNotFoundException
import cc.connectcampus.connect_campus.domain.crew.repository.CrewJoinRequestRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.util.*

@Service
@Primary
class CrewAdminServiceV0(
    val crewJoinRequestRepository: CrewJoinRequestRepository,
    val crewRepository: CrewRepository,
    val memberRepository: MemberRepository,
    val crewService: CrewServiceV0,
): CrewAdminService {

    override fun loadJoinRequest(crewId: UUID, adminId: UUID): List<CrewJoinRequest>{
        val crew: Crew = crewRepository.findById(crewId)?: throw CrewNotFoundException()

        // authorize admin else return empty list
        val admin: Member = memberRepository.findById(adminId)!!
        if(crew.admin != admin) return listOf()

        return crewJoinRequestRepository.findAllByCrewIdOrderByRequestTime(crewId)
    }

    override fun permitJoinRequest(crewJoinRequestId: UUID, adminId: UUID): UUID {
        // validate JoinRequest
        val crewJoinRequest = crewJoinRequestRepository.findById(crewJoinRequestId)?: throw CrewJoinRequestNotFoundException()

        val crew = crewJoinRequest.crew
        val member = crewJoinRequest.member

        crewService.join(crew.id!!, member.id!!)

        return member.id!!
    }

    override fun denyJoinRequest(crewJoinRequestId: UUID, adminId: UUID): UUID {
        // validate JoinRequest
        val crewJoinRequest = crewJoinRequestRepository.findById(crewJoinRequestId)?: throw CrewJoinRequestNotFoundException()

        val member = crewJoinRequest.member

        crewJoinRequestRepository.delete(crewJoinRequest)

        return member.id!!
    }

    fun checkAdminPermission(crewId: UUID, requestMemberId: UUID): Boolean {
        val crew = crewRepository.findById(crewId)?: return false
        return requestMemberId == crew.admin.id
    }
}