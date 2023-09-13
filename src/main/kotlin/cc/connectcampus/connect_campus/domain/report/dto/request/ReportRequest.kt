package cc.connectcampus.connect_campus.domain.report.dto.request

import cc.connectcampus.connect_campus.domain.report.domain.Dtype
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReportRequest(
    val dType: Dtype,
    @field:NotBlank @field:Size(min = 2, max = 4500)
    val reason: String,
)