package cc.connectcampus.connect_campus.domain.post

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.Role
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.exception.PostContentInvalidException
import cc.connectcampus.connect_campus.domain.post.exception.PostTagInvalidException
import cc.connectcampus.connect_campus.domain.post.exception.PostTitleInvalidException
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PreferenceRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostTagRepository
import cc.connectcampus.connect_campus.domain.post.service.PostCommentService0
import cc.connectcampus.connect_campus.domain.post.service.PreferenceService0
import cc.connectcampus.connect_campus.domain.post.service.PostServiceV0
import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.HandleAccessException
import org.springframework.transaction.annotation.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class PostTest (
        @Autowired val postRepository: PostRepository,
        @Autowired val postTagRepository: PostTagRepository,
        @Autowired val postService: PostServiceV0,
        @Autowired val preferenceService: PreferenceService0,
        @Autowired val memberRepository: MemberRepository,
        @Autowired val preferenceRepository: PreferenceRepository,
        @Autowired val postCommentService: PostCommentService0,
        @Autowired val postCommentRepository: PostCommentRepository,
        @Autowired val univRepository: UnivRepository,
){
    lateinit var testMember1: Member
    lateinit var testMember2: Member
    lateinit var testPost: Post
    lateinit var postTag: PostTag
    lateinit var postUpdateTag: PostTag
    @BeforeEach
    fun before(){
        testMember1 = Member.fixture()
        testMember2 = Member(
                nickname= "TestMember2",
                email = Email("test@inha.edu"),
                password = "password123",
                enrollYear = 2023,
                gender = Gender.MALE,
                createdAt = LocalDateTime.now(),
                role = Role.MEMBER,
        )
        testPost = Post.fixture()
        postTag = PostTag.fixture()
        postUpdateTag = PostTag(
                tagName = "updateTag",
        )
        postTagRepository.save(postTag)
        postTagRepository.save(postUpdateTag)
        memberRepository.save(testMember1)
        memberRepository.save(testMember2)
        univRepository.save(
                Univ(
                        name = "인하대학교",
                        emailDomain = "inha.edu",
                )
        )
    }
    @Test
    @Transactional
    fun `새로운 post 생성`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(createPost.post.id) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.title).isEqualTo(postCreationRequest.title)
        assertThat(savedPost.content).isEqualTo(postCreationRequest.content)
        assertThat(savedPost.tagId.tagName).isEqualTo(postCreationRequest.tagName)
        assertThat(savedPost.writerId).isEqualTo(testMember1)
        assertThat(createPost.commentCount).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `post 생성 title 필터 예외`() {
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "씨발",
                content = "newPostContent",
                tagName = "testTag",
        )
        // 2. 검증
        assertThrows<PostTitleInvalidException> {
            postService.create(postCreationRequest, testMember1.id!!)
        }
    }
    @Test
    @Transactional
    fun `post 생성 content 필터 예외`() {
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "씨발",
                tagName = "testTag",
        )
        // 2. 검증
        assertThrows<PostContentInvalidException> {
            postService.create(postCreationRequest, testMember1.id!!)
        }
    }
    @Test
    @Transactional
    fun `post 생성 tag 미지정 또는 DB에 없는 값 예외`() {
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "",
        )
        // 2. 검증
        assertThrows<PostTagInvalidException> {
            postService.create(postCreationRequest, testMember1.id!!)
        }
    }
    @Test
    @Transactional
    fun `post list 10개 불러오기`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        repeat(10){
            postService.create(postCreationRequest, testMember1.id!!)
        }
        // 2. 실제 데이터
        val postList = postService.readList(0)
        // 3. 비교 및 검증
        assertThat(postList.totalElements).isEqualTo(10)
        // postList.content의 각 항목이 null이 아닌지 검증
        repeat(10) {
            assertThat(postList.content[it].content).isNotNull
        }
    }
    @Test
    @Transactional
    fun `새로고침 후 다음 페이지 불러오기`(){
        // 1. 예상 데이터
        val nextCreationRequest = PostCreationRequest(
                title = "oldPostTitle",
                content = "oldPostContent",
                tagName = "testTag",
        )
        postService.create(nextCreationRequest, testMember1.id!!)
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        repeat(10){
            postService.create(postCreationRequest, testMember1.id!!)
        }
        postService.readList(0)
        // 2. 실제 데이터
        val postList = postService.readList(1)
        // 3. 비교 및 검증
        assertThat(postList.content[0].title).isEqualTo(nextCreationRequest.title)
        assertThat(postList.content[0].content).isEqualTo(nextCreationRequest.content)
    }
    @Test
    @Transactional
    fun `시간 순 정렬`(){
        // 1. 예상 데이터
        val postCreationRequest = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                createdAt = LocalDateTime.of(2019,11,11,12,12,0),
                viewCount = 0,
        )
        postRepository.save(postCreationRequest)
        val nextCreationRequest = Post(
                title = "nextPostTitle",
                content = "nextPostContent",
                tagId = postTag,
                writerId = testMember1,
                createdAt = LocalDateTime.of(2022,11,11,12,12,0),
                viewCount = 0,
        )
        postRepository.save(nextCreationRequest)
        // 2. 실제 데이터
        val postList = postService.readList(0)
        // 3. 비교 및 검증
        assertThat(postList.content[0].title).isEqualTo(nextCreationRequest.title)
        assertThat(postList.content[0].content).isEqualTo(nextCreationRequest.content)
        assertThat(postList.content[1].title).isEqualTo(postCreationRequest.title)
        assertThat(postList.content[1].content).isEqualTo(postCreationRequest.content)
    }
    @Test
    @Transactional
    fun `post 상세 페이지 불러오기`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        val getPostSingle = postService.readSingle(createPost.id!!, testMember1.id)
        // 3. 비교 및 검증
        assertThat(getPostSingle.postDetail.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.postDetail.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.postDetail.tagId.tagName).isEqualTo(postCreation.tagId.tagName)
        assertThat(getPostSingle.postDetail.writerId).isEqualTo(postCreation.writerId)
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.postDetail.viewCount).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `조회수 중복 조회`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        postService.readSingle(createPost.id!!, testMember1.id)
        val getPostSingle = postService.readSingle(createPost.id!!, testMember1.id)
        // 3. 비교 및 검증
        assertThat(getPostSingle.postDetail.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.postDetail.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.postDetail.tagId.tagName).isEqualTo(postCreation.tagId.tagName)
        assertThat(getPostSingle.postDetail.writerId).isEqualTo(postCreation.writerId)
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.postDetail.viewCount).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `조회수 +2`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        postService.readSingle(createPost.id!!, testMember1.id)
        val getPostSingle = postService.readSingle(createPost.id!!, testMember2.id)
        // 3. 비교 및 검증
        assertThat(getPostSingle.postDetail.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.postDetail.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.postDetail.tagId.tagName).isEqualTo(postCreation.tagId.tagName)
        assertThat(getPostSingle.postDetail.writerId).isEqualTo(postCreation.writerId)
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.postDetail.viewCount).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `로그인 안한 멤버가 상세페이지 조회 시`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        val getPostSingle = postService.readSingle(createPost.id!!, null)
        // 3. 비교 및 검증
        assertThat(getPostSingle.postDetail.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.postDetail.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.postDetail.tagId.tagName).isEqualTo(postCreation.tagId.tagName)
        assertThat(getPostSingle.postDetail.writerId).isEqualTo(postCreation.writerId)
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.postDetail.viewCount).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `post 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val curTime: LocalDateTime = LocalDateTime.now()
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postUpdateRequest = PostUpdateRequest(
                title = "updateTitle",
                content = "updateContent",
                tagName = "updateTag",
        )
        val updatePost = postService.update(createPost.post.id!!,postUpdateRequest, testMember1.id!!)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(updatePost.post.id) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.content).isEqualTo(postUpdateRequest.content)
        assertThat(savedPost.title).isEqualTo(postUpdateRequest.title)
        assertThat(savedPost.tagId.tagName).isEqualTo(postUpdateRequest.tagName)
        assertThat(savedPost.writerId).isEqualTo(testMember1)
        assertThat(savedPost.createdAt).isEqualTo(postRepository.findById(createPost.post.id)!!.createdAt)
        assertThat(savedPost.updatedAt).isNotEqualTo(curTime)
        assertThat(savedPost.preferences!!.size).isEqualTo(0)
        assertThat(savedPost.viewCount).isEqualTo(0)
    }
    //삭제(데이터 유무, 로그인 유무)
    @Test
    @Transactional
    fun `post 삭제`() {
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        val deletePost = postService.delete(
                createPost.id!!,
                testMember1.id!!,
        )
        // 2. 비교 및 검증
        assertThat(deletePost).isEqualTo(createPost)
        val check = postRepository.findById(createPost.id)
        assertThat(check).isNull()
    }
    @Test
    @Transactional
    fun `다른 postId 삭제 예외`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        postRepository.save(postCreation)
        val randomId = UUID.randomUUID()
        // 2. 비교 및 검증
        assertThrows<EntityNotFoundException> {
            postService.delete(
                    randomId,
                    testMember1.id!!,
            )
        }
    }
    @Test
    @Transactional
    fun `다른 회원이 post삭제 예외`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> {
            postService.delete(
                    createPost.id!!,
                    testMember2.id!!,
            )
        }
    }
    @Test
    @Transactional
    fun `게시글 좋아요 +1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val preferencePost = preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember1.id!!,
        )
        // 2. 비교 및 검증
        assertThat(preferencePost).isEqualTo(1)
        assertThat(preferenceRepository.findAll().count()).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `게시글 좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        // 좋아요 +1
        preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember1.id!!
        )
        // 좋아요 -1
        val resultPreference = preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember1.id!!
        )
        // 2. 비교 및 검증
        assertThat(resultPreference).isEqualTo(0)
        assertThat(preferenceRepository.findAll().count()).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `게시글 좋아요 +2`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember1.id!!
        )
        val resultPreference = preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember2.id!!
        )
        // 2. 비교 및 검증
        assertThat(resultPreference).isEqualTo(2)
        assertThat(preferenceRepository.findAll().count()).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `게시글 삭제 시 좋아요 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        preferenceService.postPreferenceManage(
                postId = createPost.post.id!!,
                memberId = testMember1.id!!
        )
        postService.delete(
                createPost.post.id!!,
                testMember1.id!!,
        )
        // 2. 비교 및 검증
        assertThat(postRepository.findById(createPost.post.id!!)).isNull()
        assertThat(preferenceRepository.findByPostAndMember(createPost.post, testMember1)).isNull()
    }
    @Test
    @Transactional
    fun `댓글 좋아요 +1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(
                postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest
        )
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.id!!)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedComment!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedComment.preferences!!.size).isEqualTo(1)
        assertThat(savedPreference).isNotNull()
    }
    @Test
    @Transactional
    fun `댓글 좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.id!!)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedComment!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedComment.preferences!!.size).isEqualTo(0)
        assertThat(savedPreference).isNull()
    }
    @Test
    @Transactional
    fun `댓글 좋아요 +2`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember2.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.id!!)
        val savedPreference = preferenceRepository.findAll()
        // 3. 비교 및 검증
        assertThat(savedComment!!.preferences!!.size).isEqualTo(2)
        assertThat(savedPreference.size).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `댓글 삭제 시 좋아요 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val beforeSavedComment = postCommentRepository.findById(createComment.id!!)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        postCommentService.postCommentDeletion(createComment.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.id!!)
        // 3. 비교 및 검증
        assertThat(savedComment).isNull()
        assertThat(preferenceRepository.findByCommentAndMember(beforeSavedComment!!, testMember1)).isNull()
    }
    @Test
    @Transactional
    fun `대댓글 좋아요 +1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.commentPreferenceManage(createCommentChild.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.id!!)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedCommentChild!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedCommentChild.preferences!!.size).isEqualTo(1)
        assertThat(savedPreference).isNotNull()
    }
    @Test
    @Transactional
    fun `대댓글 좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        preferenceService.commentPreferenceManage(createComment.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.id!!)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedCommentChild!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedCommentChild.preferences!!.size).isEqualTo(0)
        assertThat(savedPreference).isNull()
    }
    @Test
    @Transactional
    fun `대댓글 좋아요 +2`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.commentPreferenceManage(createCommentChild.id!!, testMember1.id!!)
        preferenceService.commentPreferenceManage(createCommentChild.id!!, testMember2.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.id!!)
        val savedPreference = preferenceRepository.findAll()
        // 3. 비교 및 검증
        assertThat(savedCommentChild!!.preferences!!.size).isEqualTo(2)
        assertThat(savedPreference.size).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `대댓글 삭제 시 좋아요 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        val beforeSavedCommentChild = postCommentRepository.findById(createCommentChild.id!!)
        preferenceService.commentPreferenceManage(createCommentChild.id!!, testMember1.id!!)
        postCommentService.postCommentDeletion(createCommentChild.id!!, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.id!!)
        // 3. 비교 및 검증
        assertThat(savedCommentChild).isNull()
        assertThat(preferenceRepository.findByCommentAndMember(beforeSavedCommentChild!!, testMember1)).isNull()
    }
    @Test
    @Transactional
    fun `댓글 생성`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.id!!) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.post).isEqualTo(createPost.post)
        assertThat(savedComment.writerId).isEqualTo(testMember1)
        assertThat(savedComment.content).isEqualTo(postCommentRequest.content)
    }
    @Test
    @Transactional
    fun `대댓글 생성`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createCommentChild.id!!) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.parentId).isEqualTo(createComment)
        assertThat(savedComment.content).isEqualTo(postCommentChildRequest.content)
        assertThat(savedComment.post).isEqualTo(createPost.post)
        assertThat(savedComment.writerId).isEqualTo(testMember1)
    }
    @Test
    @Transactional
    fun `댓글 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val curTime = LocalDateTime.now()
        val createComment = postCommentRepository.findById(postCommentService.postCommentCreate(
                postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest).id) ?: throw EntityNotFoundException()
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        val updateComment = postCommentService.postCommentUpdate(createComment.id!!, testMember1.id!!, updateCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(updateComment.id) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.id).isEqualTo(createComment.id)
        assertThat(savedComment.post).isEqualTo(createPost.post)
        assertThat(savedComment.content).isEqualTo(updateCommentRequest.content)
        assertThat(savedComment.writerId).isEqualTo(testMember1)
        assertThat(savedComment.parentId).isEqualTo(createComment.parentId)
        assertThat(savedComment.createdAt).isEqualTo(createComment.createdAt)
        assertThat(savedComment.updatedAt).isNotEqualTo(curTime)
    }
    @Test
    @Transactional
    fun `댓글 수정 필터`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentRepository.findById(postCommentService.postCommentCreate(
                postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest).id) ?: throw EntityNotFoundException()
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "씨발",
        )
        // 2. 비교 및 검증
        assertThrows<PostContentInvalidException> { postCommentService.postCommentUpdate(createComment.id!!, testMember1.id!!, updateCommentRequest) }
    }
    @Test
    @Transactional
    fun `작성자가 아닌 멤버가 댓글 수정 접근`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentRepository.findById(postCommentService.postCommentCreate(
                postId = createPost.post.id!!, memberId = testMember2.id!!, postCommentRequest).id) ?: throw EntityNotFoundException()
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> { postCommentService.postCommentUpdate(createComment.id!!, testMember1.id!!, updateCommentRequest) }
    }
    @Test
    @Transactional
    fun `대댓글 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.id!!
        )
        val curTime = LocalDateTime.now()
        val createCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        postCommentService.postCommentUpdate(createCommentChild.id!!, testMember1.id!!, updateCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createCommentChild.id!!) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.id).isEqualTo(createCommentChild.id)
        assertThat(savedComment.post).isEqualTo(createPost.post)
        assertThat(savedComment.content).isEqualTo(updateCommentRequest.content)
        assertThat(savedComment.writerId).isEqualTo(testMember1)
        assertThat(savedComment.parentId).isEqualTo(createCommentChild.parentId)
        assertThat(savedComment.createdAt).isEqualTo(createCommentChild.createdAt)
        assertThat(savedComment.updatedAt).isNotEqualTo(curTime)
    }
    @Test
    @Transactional
    fun `댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val saveComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val deleteComment = postCommentService.postCommentDeletion(saveComment.id!!, testMember1.id!!)
        // 2. 비교 및 검증
        assertThat(postCommentRepository.findById(deleteComment.id)).isNull()
    }
    @Test
    @Transactional
    fun `작성자가 아닌 회원이 댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
            title = testPost.title,
            content = testPost.content,
            tagName = testPost.tagId.tagName,
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
            content = "testComment",
            parent = null,
        )
        val saveComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> { postCommentService.postCommentDeletion(saveComment.id!!, testMember2.id!!) }
    }
    @Test
    @Transactional
    fun `대댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName
        )
        val createPost = postService.create(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment"
        )
        val saveComment = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = saveComment.id!!
        )
        val saveCommentChild = postCommentService.postCommentCreate(postId = createPost.post.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        val deleteCommentChild = postCommentService.postCommentDeletion(saveCommentChild.id!!, testMember1.id!!)
        // 2. 비교 및 검증
        assertThat(postCommentRepository.findById(deleteCommentChild.id)).isNull()
    }
}
