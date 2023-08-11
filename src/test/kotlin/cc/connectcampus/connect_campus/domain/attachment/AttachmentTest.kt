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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentTest(
    @Autowired val memberRepository: MemberRepository,
    @Autowired val attachmentService: AttachmentService,
    @Autowired val mockMvc: MockMvc,
) {
    lateinit var testUser: Member
    @BeforeEach
    fun before(){
        testUser = Member.fixture(
            nickname = "testUser",
            email = Email("ho7221@korea.ac.kr"),
        )
        memberRepository.save(testUser)
    }

    @Test
    fun `File Upload Test`() {
        val file = File("src/test/resources/profile.jpg")
        val multipartFile = MockMultipartFile(
            "file",
            file.name,
            "image/jpeg",
            file.inputStream()
        )

        mockMvc.perform(
            multipart("/attachments/image")
                .file(multipartFile)
                .param(
                    "userId", testUser.id.toString()
                )
        ).andExpect(status().isOk())
    }
}