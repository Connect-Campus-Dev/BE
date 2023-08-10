package cc.connectcampus.connect_campus.domain.attachment.service

import cc.connectcampus.connect_campus.domain.attachment.domain.Attachment
import cc.connectcampus.connect_campus.domain.attachment.dto.request.AttachmentRequest
import cc.connectcampus.connect_campus.domain.attachment.dto.response.AttachmentResponse
import cc.connectcampus.connect_campus.domain.attachment.exception.UploadFailException
import cc.connectcampus.connect_campus.domain.attachment.repository.AttachmentRepository
import com.amazonaws.services.s3.AmazonS3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
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
        val fileName = filePath + "/" + UUID.randomUUID() + "." + extension
        amazonS3Client.putObject(bucket,fileName, ByteArrayInputStream(uploadFile.bytes),null)?.let {
            throw UploadFailException()
        }
        val uploadImageUrl = amazonS3Client.getUrl(bucket,fileName).toString()
        val attachment = Attachment(attachmentId, uploadFile.fileName, uploadImageUrl)
        attachmentRepository.save(attachment)
        return AttachmentResponse(uploadImageUrl)
    }
}