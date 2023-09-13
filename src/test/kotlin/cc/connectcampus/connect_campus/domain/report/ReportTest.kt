package cc.connectcampus.connect_campus.domain.report

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.Role
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.repository.PostTagRepository
import cc.connectcampus.connect_campus.domain.report.domain.Dtype
import cc.connectcampus.connect_campus.domain.report.domain.Status
import cc.connectcampus.connect_campus.domain.report.dto.request.ReportRequest
import cc.connectcampus.connect_campus.domain.report.repository.ReportRepository
import cc.connectcampus.connect_campus.domain.report.service.ReportServiceImpl
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
class ReportTest(
    @Autowired val reportService: ReportServiceImpl,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val postTagRepository: PostTagRepository,
    @Autowired val reportRepository: ReportRepository,
) {
    lateinit var testMember1: Member
    lateinit var testMember2: Member
    lateinit var testPost: Post
    lateinit var postTag: PostTag

    @BeforeEach
    fun before() {
        testMember1 = Member.fixture()
        testMember2 = Member(
            nickname = "TestMember2",
            email = Email("test@inha.edu"),
            password = "password123",
            enrollYear = 2023,
            gender = Gender.MALE,
            createdAt = LocalDateTime.now(),
            role = Role.MEMBER,
        )
        testPost = Post.fixture()
        postTag = PostTag.fixture()
        postTagRepository.save(postTag)
        memberRepository.save(testMember1)
        memberRepository.save(testMember2)
    }

    @Test
    @Transactional
    fun `신고 요청`() {
        // 1. 예상 데이터
        val reportRequest = ReportRequest(
            dType = Dtype.Member,
            reason = "욕설",
        )
        val report = reportService.sendReport(testMember2.id!!, reportRequest, testMember1.id!!)
        // 2. 실제 데이터
        val savedReport = reportRepository.findById(report.reportId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedReport.target).isEqualTo(testMember2.id!!)
        assertThat(savedReport.dType).isEqualTo(reportRequest.dType)
        assertThat(savedReport.reason).isEqualTo(reportRequest.reason)
        assertThat(savedReport.reporter).isEqualTo(testMember1)
        assertThat(savedReport.status).isEqualTo(Status.WAITING)
    }
}