package cc.connectcampus.connect_campus.domain.attachment

import cc.connectcampus.connect_campus.domain.attachment.service.AttachmentService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
class AttachmentTest(
    @Autowired val attachmentService: AttachmentService,
) {

    @Test
    fun `File Upload Test`(){
        val file = File("src/test/resources/profile.jpg")
        attachmentService.upload(file,"test")
    }

}