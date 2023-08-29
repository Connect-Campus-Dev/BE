package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.Valid
import java.util.UUID

data class PostDeletionRequest (
        @field:Valid
        val id: UUID,
        val writerId: Member,
){
        fun toPostDeletion(postDeletionRequest: PostDeletionRequest): PostDeletionRequest{
                return PostDeletionRequest(
                        id = postDeletionRequest.id,
                        writerId = postDeletionRequest.writerId,
                )
        }
}