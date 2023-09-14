package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.CrewJoinRequest
import java.util.*

interface CrewAdminService {
    // load join request
    fun loadJoinRequest(crewId: UUID, adminId: UUID): List<CrewJoinRequest>

    // permit join request
    fun permitJoinRequest(crewJoinRequestId: UUID, adminId: UUID): UUID

    // deny join request
    fun denyJoinRequest(crewJoinRequestId: UUID, adminId: UUID): UUID
}