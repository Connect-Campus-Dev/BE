package cc.connectcampus.connect_campus.domain.attachment.controller

import cc.connectcampus.connect_campus.domain.attachment.dto.response.AttachmentResponse
import cc.connectcampus.connect_campus.domain.attachment.service.AttachmentService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RequestMapping("/attachments")
@RequiredArgsConstructor
@RestController
public class S3Controller(
    val attachmentService: AttachmentService,
) {
    @PostMapping("/image")
    fun updateUserImage(
        @RequestParam("userId") userId: UUID,
        @RequestParam("file") multipartFile: MultipartFile
    ): AttachmentResponse {
        val attachmentRequest = attachmentService.toEntity(multipartFile)
        return attachmentService.upload(attachmentRequest, "UserProfile/${userId}")
    }
}