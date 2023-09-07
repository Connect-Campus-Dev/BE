package cc.connectcampus.connect_campus.domain.post.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

class PostCommentCreationRequest(
    var parent: UUID? = null,
    @field:NotBlank @field:Size(min = 2, max = 4500)
    val content: String,
)