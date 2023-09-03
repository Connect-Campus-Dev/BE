package cc.connectcampus.connect_campus.domain.post.dto.request


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
class PostCommentUpdateRequest (
        @field:NotBlank @field:Size(min = 2, max = 4500)
        val content: String,
)