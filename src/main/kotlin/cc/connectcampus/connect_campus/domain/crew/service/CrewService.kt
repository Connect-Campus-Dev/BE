package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import java.util.*

interface CrewService {
    fun create(crewCreationRequest: CrewCreationRequest, adminId: UUID): Crew

    // 가입 요청
    fun joinRequest(crewId: UUID, memberId: UUID): Crew
    // 가입 처리 (외부에서 접근 사용 불가능해야함)
    fun join(crewId: UUID, memberId: UUID): Boolean
}