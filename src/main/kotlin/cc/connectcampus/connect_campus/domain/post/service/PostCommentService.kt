package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentDeletionRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import java.util.*

interface PostCommentService {
    fun postCommentCreate(postCommentCreationRequest: PostCommentCreationRequest) : UUID
    fun postCommentUpdate(postCommentUpdateRequest: PostCommentUpdateRequest) : UUID
    fun postCommentDeletion(postCommentDeletionRequest: PostCommentDeletionRequest) : UUID
}