package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.InputFilter
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostDeletionRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostDetailResponse
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
    override fun create(postCreationRequest: PostCreationRequest, memberId: UUID): PostDetailResponse {
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
                likeCount = 0,
                viewCount = 0,
        )
        val savePost = postRepository.save(createPost)
        //댓글 불러오기
        val commentList = postCommentRepository.findAllByPost(savePost)
        return PostDetailResponse(
                post = savePost,
                postCommentList = commentList,
                writerUniv = univService.getSchoolNameByEmailDomain(savePost.writerId.email),
                writerNickname = "익명",
                commentCount = when {
                    commentList.isEmpty() -> 0
                    else -> commentList.size
                }
        )
    }

    @Transactional
    override fun readList(page: Int): Page<Post> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending())
        return postRepository.findAll(pageable)
    }

    @Transactional
    override fun readSingle(id: UUID, viewMember: Member): PostResponse {
        val postDetail = postRepository.findById(id) ?: throw EntityNotFoundException()
        //게시글 조회수 증가
        val redisKey =  postDetail.id.toString()
        val redisUserKey = viewMember.id.toString()
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
        return PostResponse(
                postDetail = postDetail,
                postCommentList = postCommentRepository.findAllByPost(postDetail)
        )
    }

    @Transactional
    override fun update(postUpdateRequest: PostUpdateRequest): UUID {
        //데이터 검증
        val savedPost = postRepository.findById(postUpdateRequest.id) ?: throw EntityNotFoundException()
        //작성자 ID 검증
        if (savedPost.writerId!=postUpdateRequest.writerId) throw HandleAccessException()
        //입력 예외 처리
        if (InputFilter.isInputNotValid(postUpdateRequest.title)) throw PostTitleInvalidException()
        if (InputFilter.isInputNotValid(postUpdateRequest.content)) throw PostContentInvalidException()
        //태그 불러오기
        val pullPostTag = tagToPost(postUpdateRequest.tagName)
        val postUpdate = Post(
                title = postUpdateRequest.title,
                content = postUpdateRequest.content,
                writerId = postUpdateRequest.writerId,
                likeCount = savedPost.likeCount,
                viewCount = savedPost.viewCount,
                tagId = pullPostTag,
                id = savedPost.id
        )
        return postRepository.save(postUpdate).id!!
    }

    @Transactional
    override fun delete(postDeletionRequest: PostDeletionRequest): Boolean {
        //데이터 검증
        val postDetail = postRepository.findById(postDeletionRequest.id) ?: throw EntityNotFoundException()
        //작성자 ID 검증
        if (postDetail.writerId!=postDeletionRequest.writerId) throw HandleAccessException()
        postRepository.delete(postDetail)
        return true
    }

    fun tagToPost(tagName: String): PostTag {
        return postTagRepository.findByTagName(tagName) ?: throw PostTagInvalidException()
    }
}