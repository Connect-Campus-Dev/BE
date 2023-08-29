package cc.connectcampus.connect_campus.domain.attachment.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Attachment(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val uploadedURL: String? = null,
)