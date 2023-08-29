package cc.connectcampus.connect_campus.domain.post.dto.response

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostComment

data class PostResponse(
        val postDetail: Post,
        val postCommentList : MutableList<PostComment>,
)