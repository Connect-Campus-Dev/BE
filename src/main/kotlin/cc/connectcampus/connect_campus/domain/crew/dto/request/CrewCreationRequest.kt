package cc.connectcampus.connect_campus.domain.crew.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CrewCreationRequest(
    @Size(min = 2, max = 10)
    val name: String,
    @NotNull
    val adminId: UUID,
    @Size(max = 100)
    val description: String,
    @Size(min = 1, max = 5)
    val tags: List<String>
)