package cc.connectcampus.connect_campus.domain.crew.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CrewEnrollRequest(
    @Size(min = 2, max = 10)
    val name: String,
    @NotNull
    val admin: Member,
    @Size(max = 100)
    val description: String,
    @Size(min = 1, max = 5)
    val tags: List<String>
)