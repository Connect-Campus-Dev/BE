package cc.connectcampus.connect_campus.domain.attachment.dto.request

import cc.connectcampus.connect_campus.domain.attachment.exception.InvalidFileException
import cc.connectcampus.connect_campus.domain.attachment.exception.InvalidFileTypeException
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

data class AttachmentRequest(
    @field:NotBlank
    val fileName: String,

    @field:NotBlank
    val contentType: String,

    @field:Size(min=0,max=10000000)
    val size: Long,

    @field:NotBlank
    val bytes: ByteArray,
){
    fun toEntity(multipartFile: MultipartFile): AttachmentRequest{
        // validate contentType
        val allowedFileTypes: List<String> = listOf("image/jpeg", "image/jpg", "image/png")
        if(!allowedFileTypes.contains(multipartFile.contentType)) {
            throw InvalidFileTypeException()
        }

        return AttachmentRequest(
            fileName = StringUtils.cleanPath(multipartFile.originalFilename!!),
            contentType = multipartFile.contentType!!,
            size = multipartFile.size,
            bytes = multipartFile.bytes,
        )
    }
}