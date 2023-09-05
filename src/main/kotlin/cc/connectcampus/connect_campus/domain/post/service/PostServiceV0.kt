package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
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

@Service
class PostServiceV0 (
        val postRepository: PostRepository,
        val postTagRepository: PostTagRepository,
        val postCommentRepository: PostCommentRepository,
        val univService: UnivService,
        val redisTemplate: RedisTemplate<String, String>,
        val memberRepository: MemberRepository,
        ): PostService{
    @Transactional
    override fun create(postCreationRequest: PostCreationRequest, memberId: UUID): PostResponse {
        //입력 예외 처리
        if (InputFilter.isInputNotValid(postCreationRequest.title)) throw PostTitleInvalidException()
        if (InputFilter.isInputNotValid(postCreationRequest.content)) throw PostContentInvalidException()
        //태그 불러오기
        val pullPostTag = tagToPost(postCreationRequest.tagName)
        //멤버 불러오기
        val member = memberRepository.findById(memberId)
        //게시글 저장
        val createPost = Post(
                title = postCreationRequest.title,
                content = postCreationRequest.content,
                tagId = pullPostTag,
                writerId = member!!,
                viewCount = 0,
        )
        postRepository.save(createPost)
        val savedPost = postRepository.findById(createPost.id!!) ?: throw EntityNotFoundException()
        return PostResponse(
                postId = savedPost.id!!,
                title = savedPost.title,
                content = savedPost.content,
                writerNickname = univService.getSchoolNameByEmailDomain(savedPost.writerId.email),
                tagName = savedPost.tagId.tagName,
                preferenceCount = savedPost.preferences!!.size,
                viewCount = savedPost.viewCount,
                createdAt = savedPost.createdAt!!.toString(),
        )
    }

    @Transactional
    override fun readList(page: Int): Page<PostResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending())
        return postRepository.findAll(pageable).map {
            PostResponse(
                    postId = it.id!!,
                    title = it.title,
                    content = it.content,
                    writerNickname = univService.getSchoolNameByEmailDomain(it.writerId.email),
                    tagName = it.tagId.tagName,
                    preferenceCount = it.preferences!!.size,
                    viewCount = it.viewCount,
                    createdAt = formatTimeAgo(it.createdAt!!, it.updatedAt!!),
            )
        }
    }

    @Transactional
    override fun readSingle(postId: UUID, memberId: UUID?): PostResponse {
        val postDetail = postRepository.findById(postId) ?: throw EntityNotFoundException()
        val commentList = postCommentRepository.findAllByPost(postDetail)
        //로그인 안한 멤버가 상세페이지에 들어온 경우(조회수 처리 없음)
        if(memberId==null)
            return PostResponse(
                postId = postDetail.id!!,
                title = postDetail.title,
                content = postDetail.content,
                writerNickname = univService.getSchoolNameByEmailDomain(postDetail.writerId.email),
                tagName = postDetail.tagId.tagName,
                preferenceCount = postDetail.preferences!!.size,
                viewCount = postDetail.viewCount,
                commentList = commentAnonymityAndSort(commentList),
                commentCount = commentList.size,
                createdAt = formatTimeAgo(postDetail.createdAt!!, postDetail.updatedAt!!),
            )
        //로그인 한 멤버가 상세페이지에 들어온 경우(조회수 처리)
        val redisKey =  postDetail.id.toString()
        val redisUserKey = memberId.toString()
        //유저 key로 조회한 게시글 List Length
        val redisListLen = redisTemplate.opsForList().size(redisUserKey)
        //유저 key로 조회한 게시글 List
        val redisPostList =
                if (redisListLen==0L) ArrayList()
                else redisTemplate.opsForList().range(redisUserKey, 0, -1)
        //해당 게시글을 조회하지 않은 경우 조회수 + 1
        if(!redisPostList!!.contains(redisKey)) {
            postDetail.viewCount++
            postRepository.save(postDetail)
            redisTemplate.opsForList().leftPush(redisUserKey, redisKey)
        }
        //조회수 수정된 게시글 상세페이지 반환
        val updatePostDetail = postRepository.findById(postId) ?: throw EntityNotFoundException()
        return PostResponse(
            postId = updatePostDetail.id!!,
            title = updatePostDetail.title,
            content = updatePostDetail.content,
            writerNickname = univService.getSchoolNameByEmailDomain(updatePostDetail.writerId.email),
            tagName = updatePostDetail.tagId.tagName,
            preferenceCount = updatePostDetail.preferences!!.size,
            viewCount = updatePostDetail.viewCount,
            commentList = commentAnonymityAndSort(commentList),
            commentCount = commentList.size,
            createdAt = formatTimeAgo(updatePostDetail.createdAt!!, updatePostDetail.updatedAt!!),
        )
    }

    @Transactional
    override fun update(postId: UUID, postUpdateRequest: PostUpdateRequest, memberId: UUID): PostResponse {
        //데이터 검증
        val savedPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        //작성자 UUID 검증
        if (savedPost.writerId.id!=memberId) throw HandleAccessException()
        //입력 예외 처리
        if (InputFilter.isInputNotValid(postUpdateRequest.title)) throw PostTitleInvalidException()
        if (InputFilter.isInputNotValid(postUpdateRequest.content)) throw PostContentInvalidException()
        //태그 불러오기
        val pullPostTag = tagToPost(postUpdateRequest.tagName)
        val postUpdate = Post(
                title = postUpdateRequest.title,
                content = postUpdateRequest.content,
                writerId = savedPost.writerId,
                preferences = savedPost.preferences,
                viewCount = savedPost.viewCount,
                tagId = pullPostTag,
                id = savedPost.id
        )
        postRepository.save(postUpdate)
        val updatePost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        return PostResponse(
                postId = updatePost.id!!,
                title = updatePost.title,
                content = updatePost.content,
                writerNickname = univService.getSchoolNameByEmailDomain(updatePost.writerId.email),
                tagName = updatePost.tagId.tagName,
                preferenceCount = updatePost.preferences!!.size,
                viewCount = updatePost.viewCount,
                createdAt = updatePost.updatedAt!!.toString(),
        )
    }

    @Transactional
    override fun delete(postId: UUID, memberId: UUID): PostResponse {
        //데이터 검증
        val postDetail = postRepository.findById(postId) ?: throw EntityNotFoundException()
        //작성자 ID 검증
        if (postDetail.writerId.id!=memberId) throw HandleAccessException()
        postRepository.delete(postDetail)
        return PostResponse(
                postId = postDetail.id!!,
                title = postDetail.title,
                content = postDetail.content,
                writerNickname = univService.getSchoolNameByEmailDomain(postDetail.writerId.email),
                tagName = postDetail.tagId.tagName,
                preferenceCount = postDetail.preferences!!.size,
                viewCount = postDetail.viewCount,
                createdAt = postDetail.createdAt!!.toString(),
        )
    }
    @Transactional
    override fun getListByTag(tagUUID: UUID, page: Int): Page<PostResponse> {
        //태그 불러오기
        val postTag = postTagRepository.findById(tagUUID) ?: throw EntityNotFoundException()
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending())
        return postRepository.findAllByTagId(postTag, pageable).map {
            PostResponse(
                    postId = it.id!!,
                    title = it.title,
                    content = it.content,
                    writerNickname = univService.getSchoolNameByEmailDomain(it.writerId.email),
                    tagName = it.tagId.tagName,
                    preferenceCount = it.preferences!!.size,
                    viewCount = it.viewCount,
                    createdAt = formatTimeAgo(it.createdAt!!, it.updatedAt!!),
            )
        }
    }
    fun tagToPost(tagName: String): PostTag {
        return postTagRepository.findByTagName(tagName) ?: throw PostTagInvalidException()
    }
    fun formatTimeAgo(createdTime: LocalDateTime, updatedTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(createdTime, now)
        val form = when {
            duration.toMinutes() < 1 -> "방금 전"
            duration.toHours() < 1 -> "${duration.toMinutes()} 분 전"
            duration.toHours() < 24 -> "${duration.toHours()} 시간 전"
            duration.toDays() < 7 -> "${duration.toDays()} 일 전"
            duration.toDays() < 30-> "${duration.toDays() / 7} 주 전"
            duration.toDays() < 365 -> "${duration.toDays() / 30} 달 전"
            else -> "${duration.toDays() / 365} 년 전"
        }
        return form + if (createdTime != updatedTime) " (수정됨)" else ""
    }
    //댓글 정렬
    fun commentAnonymityAndSort(commentList: List<PostComment>): List<PostCommentResponse>{
        val sortedComments = commentList.sortedBy { it.createdAt }
        val anonymousCounterMap = mutableMapOf<UUID, Int>()
        val commentResponseList = mutableListOf<PostCommentResponse>()
        var anonymousPlus = 1
        var commentResponse: PostCommentResponse
        // 댓글 익명 처리 및 정렬된 순으로 댓글 리스트 생성
        sortedComments.forEach{comment ->
            //작성자 댓글 처리
            if(comment.writerId.id==comment.post.writerId.id){
                commentResponse = PostCommentResponse(
                    commentId = comment.id!!,
                    writerNickname = "글쓴이",
                    content = comment.content,
                    preferenceCount = comment.preferences!!.size,
                    createdAt = formatTimeAgo(comment.createdAt!!, comment.updatedAt!!),
                )
            }
            else {
                if (anonymousCounterMap.get(comment.writerId.id!!) == null) {
                    anonymousCounterMap.set(comment.writerId.id, anonymousPlus++)
                }
                val anonymousCounter = anonymousCounterMap.get(comment.writerId.id)
                val writerNickname = "익명${anonymousCounter}"
                commentResponse = PostCommentResponse(
                    commentId = comment.id!!,
                    writerNickname = writerNickname,
                    content = comment.content,
                    preferenceCount = comment.preferences!!.size,
                    createdAt = formatTimeAgo(comment.createdAt!!, comment.updatedAt!!),
                )
            }
            // 대댓글인 경우 부모 댓글의 childComments에 추가
            val parentComment = commentResponseList.find { it.commentId == comment.parentId?.id }
            if (parentComment != null) {
                parentComment.childComments?.add(commentResponse)
            } else {
                commentResponseList.add(commentResponse)
            }
        }
        return commentResponseList.toList()
    }

}