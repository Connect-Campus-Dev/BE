package cc.connectcampus.connect_campus.domain.post.dto.response

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostComment

data class PostDetailResponse (
        val post: Post,
        val postCommentList : MutableList<PostComment>,
        val writerUniv : String,
        val writerNickname : String,
        val commentCount: Int,
)