package cc.connectcampus.connect_campus.domain.attachment.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AttachmentRequest(
    @field:NotBlank
    val fileName: String,

    @field:NotBlank
    val contentType: String,

    @field:Size(min=0,max=10000000)
    val size: Long,

    @field:NotBlank
    val bytes: ByteArray,
)