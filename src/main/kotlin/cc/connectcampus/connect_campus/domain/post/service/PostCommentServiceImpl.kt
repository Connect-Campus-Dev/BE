package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostCommentResponse
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.domain.post.repository.PreferenceRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.HandleAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class PostCommentServiceImpl(
    val postCommentRepository: PostCommentRepository,
    val postRepository: PostRepository,
    val memberRepository: MemberRepository,
    val preferenceRepository: PreferenceRepository,
) {
    @Transactional
    fun createPostComment(
        postId: UUID,
        memberId: UUID,
        postCommentCreationRequest: PostCommentCreationRequest
    ): PostCommentResponse {
        val savedPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()

        val parentComment: PostComment? = postCommentRepository.findById(postCommentCreationRequest.parent)

        val newPostComment = PostComment(
            post = savedPost,
            writer = savedMember,
            content = postCommentCreationRequest.content,
            parent = parentComment,
            createdAt = LocalDateTime.now(),
        )

        val savedComment = postCommentRepository.save(newPostComment)

        return PostCommentResponse(
            commentId = savedComment.id!!,
            content = savedComment.content,
            writerNickname = "익명",
            createdAt = savedComment.createdAt.toString(),
            preferenceCount = savedComment.preferences.size,
        )
    }

    @Transactional
    fun updatePostComment(
        commentId: UUID,
        memberId: UUID,
        postCommentUpdateRequest: PostCommentUpdateRequest
    ): PostCommentResponse {
        val originComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        if (originComment.writer.id != memberId) throw HandleAccessException()

        originComment.isDeleted = true

        val newComment = PostComment(
            post = originComment.post,
            writer = originComment.writer,
            content = postCommentUpdateRequest.content,
            parent = originComment.parent,
            createdAt = originComment.createdAt,
            updatedAt = LocalDateTime.now(),
        )

        val updatePost = postCommentRepository.save(newComment)

        preferenceRepository.findAllByComment(originComment).forEach {
            it.comment = updatePost
            updatePost.preferences.add(it)
        }

        return PostCommentResponse(
            commentId = updatePost.id!!,
            content = updatePost.content,
            writerNickname = "익명",
            createdAt = updatePost.updatedAt.toString(),
            preferenceCount = updatePost.preferences.size,
        )
    }

    @Transactional
    fun deletePostComment(commentId: UUID, memberId: UUID): PostCommentResponse {

        val savedComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()

        if (savedComment.writer.id != memberId) throw HandleAccessException()

        savedComment.isDeleted = true

        return PostCommentResponse(
            commentId = savedComment.id!!,
            content = savedComment.content,
            writerNickname = "익명",
            createdAt = savedComment.createdAt.toString(),
            preferenceCount = savedComment.preferences.size,
        )
    }
}