package cc.connectcampus.connect_campus.domain.report.service

import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.report.domain.Report
import cc.connectcampus.connect_campus.domain.report.dto.request.ReportRequest
import cc.connectcampus.connect_campus.domain.report.dto.response.ReportResponse
import cc.connectcampus.connect_campus.domain.report.repository.ReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReportServiceImpl(
    val reportRepository: ReportRepository,
    val memberRepository: MemberRepository,
) {
    @Transactional
    fun sendReport(targetId: UUID, reportRequest: ReportRequest, reporterId: UUID): ReportResponse {
        val reporter = memberRepository.findById(reporterId) ?: throw MemberNotFoundException()

        val newReport = Report(
            target = targetId,
            dType = reportRequest.dType,
            reporter = reporter,
            reason = reportRequest.reason,
        )

        reportRepository.save(newReport)

        return ReportResponse(
            reportId = newReport.id!!,
            dType = newReport.dType,
            reason = newReport.reason,
            createdAt = newReport.createdAt,
            status = newReport.status,
        )
    }
}