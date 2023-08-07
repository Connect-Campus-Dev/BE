package cc.connectcampus.connect_campus.domain.member.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CodeVerificationRequest(
    @field:Size(min=4, max=4)
    val code: String,
    @NotBlank
    val id: UUID,
)