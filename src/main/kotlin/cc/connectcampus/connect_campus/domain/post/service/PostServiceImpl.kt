package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostCommentResponse
import cc.connectcampus.connect_campus.domain.post.dto.response.PostResponse
import cc.connectcampus.connect_campus.domain.post.exception.*
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostTagRepository
import cc.connectcampus.connect_campus.domain.post.repository.PreferenceRepository
import cc.connectcampus.connect_campus.domain.univ.service.UnivService
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.HandleAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Service
class PostServiceImpl(
    val postRepository: PostRepository,
    val postTagRepository: PostTagRepository,
    val postCommentRepository: PostCommentRepository,
    val univService: UnivService,
    val redisTemplate: RedisTemplate<String, String>,
    val memberRepository: MemberRepository,
    val preferenceRepository: PreferenceRepository,
) {
    @Transactional
    fun createPost(postCreationRequest: PostCreationRequest, memberId: UUID): PostResponse {
        val postTag = getPostTag(postCreationRequest.tagName)
        val member = memberRepository.findById(memberId) ?: throw MemberNotFoundException()
        val newPost = Post(
            title = postCreationRequest.title,
            content = postCreationRequest.content,
            tag = postTag,
            writer = member,
            viewCount = 0,
        )

        val savedPost = postRepository.save(newPost)

        return PostResponse(
            postId = savedPost.id!!,
            title = savedPost.title,
            content = savedPost.content,
            writerSchoolName = univService.getSchoolNameByEmailDomain(savedPost.writer.email),
            tagName = savedPost.tag.tagName,
            preferenceCount = savedPost.preferences.size,
            viewCount = savedPost.viewCount,
            createdAt = savedPost.createdAt.toString(),
        )
    }

    @Transactional
    fun getPostList(page: Int): Page<PostResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending())
        return postRepository.findAllByIsDeletedFalse(pageable).map {
            PostResponse(
                postId = it.id!!,
                title = it.title,
                content = it.content,
                writerSchoolName = univService.getSchoolNameByEmailDomain(it.writer.email),
                tagName = it.tag.tagName,
                preferenceCount = it.preferences.size,
                viewCount = it.viewCount,
                createdAt = formatTimeAgo(it.createdAt, it.updatedAt),
            )
        }
    }

    @Transactional
    fun getPostListByTag(tagName: String, page: Int): Page<PostResponse> {
        val postTag = postTagRepository.findByTagName(tagName) ?: throw PostTagInvalidException()
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending())
        return postRepository.findAllByTagAndIsDeletedFalse(postTag, pageable).map {
            PostResponse(
                postId = it.id!!,
                title = it.title,
                content = it.content,
                writerSchoolName = univService.getSchoolNameByEmailDomain(it.writer.email),
                tagName = it.tag.tagName,
                preferenceCount = it.preferences.size,
                viewCount = it.viewCount,
                createdAt = formatTimeAgo(it.createdAt, it.updatedAt),
            )
        }
    }

    @Transactional
    fun getPostDetail(postId: UUID, memberId: UUID): PostResponse {
        val postDetail = postRepository.findById(postId) ?: throw EntityNotFoundException()

        if (postDetail.isDeleted) throw EntityNotFoundException()

        val commentList = postCommentRepository.findAllByPost(postDetail)

        val redisKey = postDetail.id.toString()
        val redisUserKey = memberId.toString()
        val redisListLen = redisTemplate.opsForList().size(redisUserKey) ?: 0L
        val redisPostList =
            if (redisListLen == 0L) ArrayList()
            else redisTemplate.opsForList().range(redisUserKey, 0, -1)
        if (!redisPostList!!.contains(redisKey)) {
            postDetail.viewCount++
            redisTemplate.opsForList().leftPush(redisUserKey, redisKey)
        }

        return PostResponse(
            postId = postDetail.id!!,
            title = postDetail.title,
            content = postDetail.content,
            writerSchoolName = univService.getSchoolNameByEmailDomain(postDetail.writer.email),
            tagName = postDetail.tag.tagName,
            preferenceCount = postDetail.preferences.size,
            viewCount = postDetail.viewCount,
            commentList = commentAnonymityAndSort(commentList),
            commentCount = commentList.size,
            createdAt = formatTimeAgo(postDetail.createdAt, postDetail.updatedAt),
        )
    }

    @Transactional
    fun updatePost(postId: UUID, postUpdateRequest: PostUpdateRequest, memberId: UUID): PostResponse {
        val originPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        if (originPost.writer.id != memberId) throw HandleAccessException()
        val postTag = getPostTag(postUpdateRequest.tagName)

        originPost.isDeleted = true

        val newPost = Post(
            title = postUpdateRequest.title,
            content = postUpdateRequest.content,
            tag = postTag,
            writer = originPost.writer,
            viewCount = originPost.viewCount,
            createdAt = originPost.createdAt,
            updatedAt = LocalDateTime.now(),
        )

        val updatedPost = postRepository.save(newPost)

        preferenceRepository.findAllByPost(originPost).forEach {
            it.post = updatedPost
            updatedPost.preferences.add(it)
        }

        postCommentRepository.findAllByPost(originPost).forEach {
            it.post = updatedPost
        }

        return PostResponse(
            postId = updatedPost.id!!,
            title = updatedPost.title,
            content = updatedPost.content,
            writerSchoolName = univService.getSchoolNameByEmailDomain(updatedPost.writer.email),
            tagName = updatedPost.tag.tagName,
            preferenceCount = updatedPost.preferences.size,
            viewCount = updatedPost.viewCount,
            createdAt = updatedPost.updatedAt.toString(),
        )
    }

    @Transactional
    fun deletePost(postId: UUID, memberId: UUID): PostResponse {

        val postDetail = postRepository.findById(postId) ?: throw EntityNotFoundException()
        if (postDetail.writer.id != memberId) throw HandleAccessException()

        postDetail.isDeleted = true

        return PostResponse(
            postId = postDetail.id!!,
            title = postDetail.title,
            content = postDetail.content,
            writerSchoolName = univService.getSchoolNameByEmailDomain(postDetail.writer.email),
            tagName = postDetail.tag.tagName,
            preferenceCount = postDetail.preferences.size,
            viewCount = postDetail.viewCount,
            createdAt = postDetail.createdAt.toString(),
        )
    }

    @Transactional
    fun searchPost(searchWord: String, page: Int): Page<PostResponse> {
        if (searchWord == "") throw PostSearchInvalidException()
        val pageable: Pageable = PageRequest.of(page, 10)
        return postRepository.searchByTitleAndContentContaining(searchWord, pageable).map {
            PostResponse(
                postId = it.id!!,
                title = it.title,
                content = it.content,
                writerSchoolName = univService.getSchoolNameByEmailDomain(it.writer.email),
                tagName = it.tag.tagName,
                preferenceCount = it.preferences.size,
                viewCount = it.viewCount,
                createdAt = formatTimeAgo(it.createdAt, it.updatedAt),
            )
        }
    }

    fun getPostTag(tagName: String): PostTag {
        return postTagRepository.findByTagName(tagName) ?: throw PostTagInvalidException()
    }

    fun formatTimeAgo(createdTime: LocalDateTime, updatedTime: LocalDateTime?): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(createdTime, now)
        val form = when {
            duration.toMinutes() < 1 -> "방금 전"
            duration.toHours() < 1 -> "${duration.toMinutes()} 분 전"
            duration.toHours() < 24 -> "${duration.toHours()} 시간 전"
            duration.toDays() < 7 -> "${duration.toDays()} 일 전"
            duration.toDays() < 30 -> "${duration.toDays() / 7} 주 전"
            duration.toDays() < 365 -> "${duration.toDays() / 30} 달 전"
            else -> "${duration.toDays() / 365} 년 전"
        }
        return form + if (updatedTime == null) "" else " (수정됨)"
    }

    //댓글 정렬
    fun commentAnonymityAndSort(commentList: List<PostComment>): List<PostCommentResponse> {
        val sortedComments = commentList.sortedBy { it.createdAt }
        val anonymousCounterMap = mutableMapOf<UUID, Int>()
        val commentResponseList = mutableListOf<PostCommentResponse>()
        var anonymousPlus = 1
        var commentResponse: PostCommentResponse
        // 댓글 익명 처리 및 정렬된 순으로 댓글 리스트 생성
        sortedComments.forEach { comment ->
            //작성자 댓글 처리
            if (comment.writer.id == comment.post.writer.id) {
                commentResponse = PostCommentResponse(
                    commentId = comment.id!!,
                    writerNickname = "글쓴이",
                    content = if (comment.isDeleted) null else comment.content,
                    preferenceCount = comment.preferences.size,
                    createdAt = formatTimeAgo(comment.createdAt, comment.updatedAt),
                )
            } else {
                if (anonymousCounterMap.get(comment.writer.id!!) == null) {
                    anonymousCounterMap.set(comment.writer.id, anonymousPlus++)
                }
                val anonymousCounter = anonymousCounterMap.get(comment.writer.id)
                val writerNickname = "익명${anonymousCounter}"
                commentResponse = PostCommentResponse(
                    commentId = comment.id!!,
                    writerNickname = writerNickname,
                    content = if (comment.isDeleted) null else comment.content,
                    preferenceCount = comment.preferences.size,
                    createdAt = formatTimeAgo(comment.createdAt, comment.updatedAt),
                )
            }
            // 대댓글인 경우 부모 댓글의 childComments에 추가
            val parentComment = commentResponseList.find { it.commentId == comment.parent?.id }
            if (parentComment != null) {
                parentComment.childComments.add(commentResponse)
            } else {
                commentResponseList.add(commentResponse)
            }
        }
        return commentResponseList.toList()
    }

}