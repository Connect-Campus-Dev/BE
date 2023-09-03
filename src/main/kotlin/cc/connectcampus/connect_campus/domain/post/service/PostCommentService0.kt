package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCommentUpdateRequest
import cc.connectcampus.connect_campus.domain.post.exception.PostContentInvalidException
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.HandleAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
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

    override fun postCommentUpdate(commentId: UUID, memberId: UUID, postCommentUpdateRequest: PostCommentUpdateRequest): PostComment {
        //댓글 호출
        val savedComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        //멤버 호출
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()
        //작성자 검증
        if(savedMember!=savedComment.writerId) throw HandleAccessException()
        //수정 내용 검증
        if(InputFilter.isInputNotValid(postCommentUpdateRequest.content)) throw PostContentInvalidException()
        savedComment.content = postCommentUpdateRequest.content
        savedComment.updatedAt = LocalDateTime.now()
        return postCommentRepository.save(savedComment)
    }

    override fun postCommentDeletion(commentId: UUID, memberId: UUID): PostComment {
        //댓글 호출
        val savedComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        //멤버 호출
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()
        //작성자 검증
        if(savedMember!=savedComment.writerId) throw HandleAccessException()
        postCommentRepository.delete(savedComment)
        return savedComment
    }
}