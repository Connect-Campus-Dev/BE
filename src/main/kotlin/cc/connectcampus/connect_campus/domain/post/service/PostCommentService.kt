package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentDeletionRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import java.util.*

interface PostCommentService {
    fun postCommentCreate(postId: UUID, memberId: UUID, postCommentCreationRequest: PostCommentCreationRequest) : PostComment
    fun postCommentUpdate(commentId: UUID, memberId: UUID, postCommentUpdateRequest: PostCommentUpdateRequest) : PostComment
    fun postCommentDeletion(postCommentDeletionRequest: PostCommentDeletionRequest) : UUID
}