package cc.connectcampus.connect_campus.domain.attachment.service

import cc.connectcampus.connect_campus.domain.attachment.domain.Attachment
import cc.connectcampus.connect_campus.domain.attachment.dto.request.AttachmentRequest
import cc.connectcampus.connect_campus.domain.attachment.dto.response.AttachmentResponse
import cc.connectcampus.connect_campus.domain.attachment.exception.InvalidFileTypeException
import cc.connectcampus.connect_campus.domain.attachment.exception.UploadFailException
import cc.connectcampus.connect_campus.domain.attachment.repository.AttachmentRepository
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.util.*


@Service
class AttachmentService(
    val amazonS3Client: AmazonS3Client,
    val attachmentRepository: AttachmentRepository,
) {
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String? = null

    @Transactional
    fun upload(uploadFile: AttachmentRequest, filePath: String): AttachmentResponse {
        val attachmentId = UUID.randomUUID()
        val extension = uploadFile.fileName.substring(uploadFile.fileName.lastIndexOf("."))
        val fileName = filePath + "/" + UUID.randomUUID() + extension
        val md = MessageDigest.getInstance("md5")

        val objectMetadata = ObjectMetadata()
        objectMetadata.contentLength = uploadFile.size
        objectMetadata.contentType = uploadFile.contentType
        objectMetadata.contentMD5 = Base64.getEncoder().encodeToString(md.digest(uploadFile.bytes))
        val putobjectRequest = PutObjectRequest(
            bucket,
            fileName,
            ByteArrayInputStream(uploadFile.bytes),
            objectMetadata
        )

        amazonS3Client.putObject(putobjectRequest)?: throw UploadFailException()
        val uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString()
        val attachment = Attachment(attachmentId, uploadFile.fileName, uploadImageUrl)
        attachmentRepository.save(attachment)
        return AttachmentResponse(uploadImageUrl)
    }

    fun toEntity(multipartFile: MultipartFile): AttachmentRequest {
        // validate contentType
        val allowedFileTypes: List<String> = listOf("image/jpeg", "image/jpg", "image/png")
        if (!allowedFileTypes.contains(multipartFile.contentType)) {
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