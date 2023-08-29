package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class PostCommentCreationRequest (
        val post: Post,
        val writerId: Member,
        val parent: PostComment?,
        @field:NotBlank @field:Size(min = 2, max = 4500)
        val content: String,
)