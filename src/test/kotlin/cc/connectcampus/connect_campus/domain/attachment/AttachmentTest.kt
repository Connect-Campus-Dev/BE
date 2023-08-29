package cc.connectcampus.connect_campus.domain.attachment

import cc.connectcampus.connect_campus.domain.attachment.service.AttachmentService
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import java.io.File
import org.assertj.core.api.Assertions.*

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentTest(
    @Autowired val memberRepository: MemberRepository,
    @Autowired val attachmentService: AttachmentService,
) {
    lateinit var testUser: Member
    @BeforeEach
    fun before(){
        testUser = Member.fixture(
            nickname = "testUser",
            email = Email("ho7221@korea.ac.kr"),
        )
    }

    @Test
    @Transactional
    fun `File Upload Test`() {
        memberRepository.save(testUser)
        val file = File("src/test/resources/test_profile.png")
        val multipartFile = MockMultipartFile(
            "file",
            file.name,
            "image/jpeg",
            file.inputStream()
        )
        val result = attachmentService.upload(attachmentService.toEntity(multipartFile), "UserProfile/${testUser.id}")
        assertThat(result.uploadedURL).isNotNull
    }
}