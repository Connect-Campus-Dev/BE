package cc.connectcampus.connect_campus.domain.post.dto.response

import java.util.*

data class PostResponse(
        val postId: UUID,
        val title: String,
        val content: String,
        val writerSchoolName: String,
        val tagName: String,
        val preferenceCount: Int = 0,
        val commentCount: Int = 0,
        val viewCount: Int = 0,
        val commentList: List<PostCommentResponse> = mutableListOf(),
        val createdAt: String,
)