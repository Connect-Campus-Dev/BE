package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentDeletionRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import cc.connectcampus.connect_campus.domain.post.exception.PostCommentLengthInvalid
import cc.connectcampus.connect_campus.domain.post.exception.PostContentInvalidException
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.HandleAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostCommentService0 (
        val postCommentRepository: PostCommentRepository,
        val postRepository: PostRepository,
        val memberRepository: MemberRepository,
): PostCommentService{
    @Transactional
    override fun postCommentCreate(postId: UUID, memberId: UUID, postCommentCreationRequest: PostCommentCreationRequest) : PostComment {
        //게시글 검증
        val savedPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        //멤버 불러오기
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()
        //모댓글인 경우 null, 대댓글인 경우 모댓글 반환
        val savedComment = postCommentRepository.findById(postCommentCreationRequest.parent)
        //content 검증
        if(InputFilter.isInputNotValid(postCommentCreationRequest.content)) throw PostContentInvalidException()
        val createPostComment = PostComment(
                post = savedPost,
                writerId = savedMember,
                content = postCommentCreationRequest.content,
                parentId = savedComment,
        )
        return postCommentRepository.save(createPostComment)
    }

    override fun postCommentUpdate(postCommentUpdateRequest: PostCommentUpdateRequest): UUID {
        postCommentRepository.findById(postCommentUpdateRequest.id)
        if(InputFilter.isInputNotValid(postCommentUpdateRequest.content)) throw PostContentInvalidException()
        val updatePostComment = PostComment(
                id = postCommentUpdateRequest.id,
                post = postCommentUpdateRequest.postId,
                writerId = postCommentUpdateRequest.writerId,
                content = postCommentUpdateRequest.content,
        )
        return postCommentRepository.save(updatePostComment).id!!
    }

    override fun postCommentDeletion(postCommentDeletionRequest: PostCommentDeletionRequest): UUID {
        //데이터 검증
        val postComment = postCommentRepository.findById(postCommentDeletionRequest.id) ?: throw EntityNotFoundException()
        //작성자 ID 검증
        if (postComment.writerId!=postCommentDeletionRequest.writerId) throw HandleAccessException()
        postCommentRepository.delete(postComment)
        return postComment.id!!
    }

}