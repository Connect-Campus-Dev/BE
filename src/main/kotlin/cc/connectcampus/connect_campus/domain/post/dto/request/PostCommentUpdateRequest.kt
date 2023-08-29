package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

class PostCommentUpdateRequest (
        @field:Valid
        val id: UUID,
        val writerId: Member,
        val postId: Post,
        @field:NotBlank @field:Size(min = 2, max = 4500)
        val content: String,
)