package cc.connectcampus.connect_campus.domain.report.controller

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.report.dto.request.ReportRequest
import cc.connectcampus.connect_campus.domain.report.dto.response.ReportResponse
import cc.connectcampus.connect_campus.domain.report.service.ReportServiceImpl
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/report")
class ReportController(
    val reportService: ReportServiceImpl,
) {
    @PostMapping("/{targetId}")
    fun sendReport(
        @PathVariable targetId: UUID,
        @RequestBody reportRequest: ReportRequest,
        authentication: Authentication
    ): ReportResponse {
        val reporterId: UUID = getMemberId(authentication)
        return reportService.sendReport(targetId, reportRequest, reporterId)
    }

    private fun getMemberId(authentication: Authentication): UUID {
        val customUser = authentication.principal as CustomUser
        return customUser.id!!
    }
}