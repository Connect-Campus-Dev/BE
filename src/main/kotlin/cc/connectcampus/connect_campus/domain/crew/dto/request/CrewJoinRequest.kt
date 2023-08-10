package cc.connectcampus.connect_campus.domain.crew.dto.request

import jakarta.validation.constraints.NotNull
import java.util.*

data class CrewJoinRequest(
    @field:NotNull
    val crewId: UUID,
    @field:NotNull
    val memberId: UUID,
)