package cc.connectcampus.connect_campus.domain.post.dto.response

import java.util.*

//처음 생성을 할 때 필요없는 값 제외
data class PostResponse(
        val postId: UUID,
        val title: String,
        val content: String,
        val writerNickname: String,
        val tagName: String,
        val preferenceCount: Int,
        val commentCount: Int? = 0,
        val viewCount: Int? = 0,
        val commentList: List<PostCommentResponse>? = null,
        val createdAt: String,
)