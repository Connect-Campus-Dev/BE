package cc.connectcampus.connect_campus.domain.report.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "target_id")
    val target: UUID,

    @Enumerated(EnumType.STRING)
    @Column
    val dType: Dtype,

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    val reporter: Member,

    @Column
    val reason: String,

    @Column
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column
    val status: Status = Status.WAITING,

    @Column
    val result: String? = null,

    @Column
    val resolvedAt: LocalDateTime? = null,
) {
    companion object {
        fun fixture(
            id: UUID? = null,
            target: UUID = UUID.randomUUID(),
            dType: Dtype = Dtype.Member,
            reporter: Member = Member.fixture(),
            reason: String = "testReason",
            createdAt: LocalDateTime = LocalDateTime.now(),
            status: Status = Status.WAITING,
            result: String? = null,
            resolvedAt: LocalDateTime? = null,
        ): Report {
            return Report(id, target, dType, reporter, reason, createdAt, status, result, resolvedAt)
        }
    }
}