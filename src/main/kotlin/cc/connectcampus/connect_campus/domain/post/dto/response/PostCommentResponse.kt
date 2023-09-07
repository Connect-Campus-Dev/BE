package cc.connectcampus.connect_campus.domain.post.dto.response

import java.util.*

data class PostCommentResponse(
    val commentId: UUID,
    val content: String,
    val writerNickname: String,
    val preferenceCount: Int,
    val createdAt: String,
    val childComments: MutableList<PostCommentResponse> = mutableListOf(),
)