package cc.connectcampus.connect_campus.domain.report.repository

import cc.connectcampus.connect_campus.domain.report.domain.Report
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReportRepository : JpaRepository<Report, Long> {
    fun findById(id: UUID): Report?
}