package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import java.util.*

interface CrewService {
    fun create(crewCreationRequest: CrewCreationRequest): Crew
    fun join(crewId: UUID, memberId: UUID): Crew
}