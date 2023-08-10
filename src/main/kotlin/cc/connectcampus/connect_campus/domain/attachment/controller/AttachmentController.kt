package cc.connectcampus.connect_campus.domain.attachment.controller

import cc.connectcampus.connect_campus.domain.attachment.service.AttachmentService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequiredArgsConstructor
@RestController
public class S3Controller(
    val attachmentService: AttachmentService
) {
    @PostMapping("/{userId}/image")
    fun updateUserImage(@RequestParam("images") multipartFile: MultipartFile) {
        attachmentService.uploadFiles(multipartFile, "static")
    }
}