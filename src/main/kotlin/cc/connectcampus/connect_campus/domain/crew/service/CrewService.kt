package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewEnrollRequest
import cc.connectcampus.connect_campus.domain.member.domain.Member

interface CrewService {
    fun enroll(crewEnrollRequest: CrewEnrollRequest): Crew
    fun joinCrew(crew: Crew, member: Member): Crew
}