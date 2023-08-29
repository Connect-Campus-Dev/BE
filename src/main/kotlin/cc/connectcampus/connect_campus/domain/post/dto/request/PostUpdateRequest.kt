package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class PostUpdateRequest (
        @field:Valid
        val id: UUID,
        @field:NotBlank @field:Size(min = 2, max = 60)
        val title: String,
        @field:NotBlank @field:Size(min = 2, max = 4500)
        val content: String,
        @field:NotBlank @field:Size(min = 2)
        val tagName: String,
        val writerId: Member,
){
    companion object{
        fun toPostCorrection(postUpdateRequest: PostUpdateRequest): PostUpdateRequest{
            return PostUpdateRequest(
                    id = postUpdateRequest.id,
                    title = postUpdateRequest.title,
                    content = postUpdateRequest.content,
                    tagName = postUpdateRequest.tagName,
                    writerId = postUpdateRequest.writerId,
            )
        }
    }
}