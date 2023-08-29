package cc.connectcampus.connect_campus.domain.attachment.repository

import cc.connectcampus.connect_campus.domain.attachment.domain.Attachment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository: JpaRepository<Attachment, Long> {
}