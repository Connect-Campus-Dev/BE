package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostCommentResponse
import java.util.*

interface PostCommentService {
    fun postCommentCreate(postId: UUID, memberId: UUID, postCommentCreationRequest: PostCommentCreationRequest) : PostCommentResponse
    fun postCommentUpdate(commentId: UUID, memberId: UUID, postCommentUpdateRequest: PostCommentUpdateRequest) : PostCommentResponse
    fun postCommentDeletion(commentId: UUID, memberId: UUID) : PostCommentResponse
}