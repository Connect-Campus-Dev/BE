package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.Valid
import java.util.*

class PostCommentDeletionRequest (
        @field:Valid
        val id: UUID,
        val writerId: Member,
        )