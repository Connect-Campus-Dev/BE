package cc.connectcampus.connect_campus.domain.report.dto.response

import cc.connectcampus.connect_campus.domain.report.domain.Dtype
import cc.connectcampus.connect_campus.domain.report.domain.Status
import java.time.LocalDateTime
import java.util.*

data class ReportResponse(
    val reportId: UUID,
    val dType: Dtype,
    val reason: String,
    val createdAt: LocalDateTime,
    val status: Status,
)