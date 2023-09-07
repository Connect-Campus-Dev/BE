package cc.connectcampus.connect_campus.domain.post

import cc.connectcampus.connect_campus.domain.member.domain.Gender
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.domain.Role
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.exception.PostTagInvalidException
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PreferenceRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostTagRepository
import cc.connectcampus.connect_campus.domain.post.service.PostCommentServiceImpl
import cc.connectcampus.connect_campus.domain.post.service.PreferenceServiceImpl
import cc.connectcampus.connect_campus.domain.post.service.PostServiceImpl
import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.domain.univ.service.UnivService
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
    @Autowired val postService: PostServiceImpl,
    @Autowired val preferenceService: PreferenceServiceImpl,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val preferenceRepository: PreferenceRepository,
    @Autowired val postCommentService: PostCommentServiceImpl,
    @Autowired val postCommentRepository: PostCommentRepository,
    @Autowired val univRepository: UnivRepository,
    @Autowired val univService: UnivService,
){
    lateinit var testMember1: Member
    lateinit var testMember2: Member
    lateinit var testMember3: Member
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
        testMember3 = Member(
            nickname = "TestMember3",
            email = Email("test1@inha.edu"),
            password = "password1234",
            enrollYear = 2022,
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
        memberRepository.save(testMember3)
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
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(createPost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.title).isEqualTo(postCreationRequest.title)
        assertThat(savedPost.content).isEqualTo(postCreationRequest.content)
        assertThat(savedPost.tag.tagName).isEqualTo(postCreationRequest.tagName)
        assertThat(savedPost.writer).isEqualTo(testMember1)
        assertThat(createPost.commentCount).isEqualTo(0)
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
            postService.createPost(postCreationRequest, testMember1.id!!)
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
            postService.createPost(postCreationRequest, testMember1.id!!)
        }
        // 2. 실제 데이터
        val postList = postService.getPostList(0)
        // 3. 비교 및 검증
        assertThat(postList.totalElements).isEqualTo(10)
        // postList.content의 각 항목이 null이 아닌지 검증
        repeat(10) {
            assertThat(postList.content[it].content).isNotNull
        }
    }
    @Test
    @Transactional
    fun `삭제된 게시글 제외하고 목록 가져오기`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
            title = "newPostTitle",
            content = "newPostContent",
            tagName = "testTag",
        )
        val savedPost = postService.createPost(postCreationRequest, testMember1.id!!)
        repeat(9){
            postService.createPost(postCreationRequest, testMember1.id!!)
        }
        postService.deletePost(
            savedPost.postId,
            testMember1.id!!,
        )
        // 2. 실제 데이터
        val postList = postService.getPostList(0)
        // 3. 비교 및 검증
        assertThat(postList.totalElements).isEqualTo(9)
        // postList.content의 각 항목이 null이 아닌지 검증
        repeat(9) {
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
        postService.createPost(nextCreationRequest, testMember1.id!!)
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
        )
        repeat(10){
            postService.createPost(postCreationRequest, testMember1.id!!)
        }
        postService.getPostList(0)
        // 2. 실제 데이터
        val postList = postService.getPostList(1)
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
                tag = postTag,
                writer = testMember1,
                createdAt = LocalDateTime.of(2019,11,11,12,12,0),
                viewCount = 0,
        )
        postRepository.save(postCreationRequest)
        val nextCreationRequest = Post(
                title = "nextPostTitle",
                content = "nextPostContent",
                tag = postTag,
                writer = testMember1,
                createdAt = LocalDateTime.of(2022,11,11,12,12,0),
                viewCount = 0,
        )
        postRepository.save(nextCreationRequest)
        // 2. 실제 데이터
        val postList = postService.getPostList(0)
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
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        val getPostSingle = postService.getPostDetail(createPost.id!!, testMember1.id!!)
        // 3. 비교 및 검증
        assertThat(getPostSingle.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.tagName).isEqualTo(postCreation.tag.tagName)
        assertThat(getPostSingle.writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.viewCount).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `조회수 중복 조회`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        postService.getPostDetail(createPost.id!!, testMember1.id!!)
        val getPostSingle = postService.getPostDetail(createPost.id!!, testMember1.id!!)
        // 3. 비교 및 검증
        assertThat(getPostSingle.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.tagName).isEqualTo(postCreation.tag.tagName)
        assertThat(getPostSingle.writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.viewCount).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `조회수 +2`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        postService.getPostDetail(createPost.id!!, testMember1.id!!)
        val getPostSingle = postService.getPostDetail(createPost.id!!, testMember2.id!!)
        // 3. 비교 및 검증
        assertThat(getPostSingle.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.tagName).isEqualTo(postCreation.tag.tagName)
        assertThat(getPostSingle.writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getPostSingle.commentCount).isEqualTo(0)
        assertThat(getPostSingle.viewCount).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `post 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postUpdateRequest = PostUpdateRequest(
                title = "updateTitle",
                content = "updateContent",
                tagName = "updateTag",
        )
        val updatePost = postService.updatePost(createPost.postId,postUpdateRequest, testMember1.id!!)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(updatePost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.content).isEqualTo(postUpdateRequest.content)
        assertThat(savedPost.title).isEqualTo(postUpdateRequest.title)
        assertThat(savedPost.tag.tagName).isEqualTo(postUpdateRequest.tagName)
        assertThat(savedPost.writer).isEqualTo(testMember1)
        assertThat(savedPost.createdAt).isEqualTo(postRepository.findById(createPost.postId)!!.createdAt)
        assertThat(savedPost.updatedAt).isNotEqualTo(savedPost.createdAt)
        assertThat(savedPost.preferences.size).isEqualTo(0)
        assertThat(savedPost.viewCount).isEqualTo(0)
    }

    @Test
    @Transactional
    fun `post 삭제`() {
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        postService.deletePost(
                createPost.id!!,
                testMember1.id!!,
        )
        // 2. 비교 및 검증
        val savedPost = postRepository.findById(createPost.id)
        assertThat(savedPost!!.isDeleted).isTrue()
    }
    @Test
    @Transactional
    fun `다른 postId 삭제 예외`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        postRepository.save(postCreation)
        val randomId = UUID.randomUUID()
        // 2. 비교 및 검증
        assertThrows<EntityNotFoundException> {
            postService.deletePost(
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
                tag = postTag,
                writer = testMember1,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> {
            postService.deletePost(
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
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val preferencePost = preferenceService.preferPost(
                postId = createPost.postId,
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
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        // 좋아요 +1
        preferenceService.preferPost(
                postId = createPost.postId,
                memberId = testMember1.id!!
        )
        // 좋아요 -1
        val resultPreference = preferenceService.preferPost(
                postId = createPost.postId,
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
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        preferenceService.preferPost(
                postId = createPost.postId,
                memberId = testMember1.id!!
        )
        val resultPreference = preferenceService.preferPost(
                postId = createPost.postId,
                memberId = testMember2.id!!
        )
        // 2. 비교 및 검증
        assertThat(resultPreference).isEqualTo(2)
        assertThat(preferenceRepository.findAll().count()).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `댓글 좋아요 +1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(
                postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest
        )
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.commentId)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedComment!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedComment.preferences.size).isEqualTo(1)
        assertThat(savedPreference).isNotNull()
    }
    @Test
    @Transactional
    fun `댓글 좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.commentId)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedComment!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedComment.preferences.size).isEqualTo(0)
        assertThat(savedPreference).isNull()
    }
    @Test
    @Transactional
    fun `댓글 좋아요 +2`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        preferenceService.preferComment(createComment.commentId, testMember2.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.commentId)
        val savedPreference = preferenceRepository.findAll()
        // 3. 비교 및 검증
        assertThat(savedComment!!.preferences.size).isEqualTo(2)
        assertThat(savedPreference.size).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `댓글 삭제 시 좋아요 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val beforeSavedComment = postCommentRepository.findById(createComment.commentId)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        postCommentService.deletePostComment(createComment.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.commentId)
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
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.preferComment(createCommentChild.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.commentId)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedCommentChild!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedCommentChild.preferences.size).isEqualTo(1)
        assertThat(savedPreference).isNotNull()
    }
    @Test
    @Transactional
    fun `대댓글 좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        preferenceService.preferComment(createComment.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.commentId)
        val savedPreference = preferenceRepository.findByCommentAndMember(savedCommentChild!!, testMember1)
        // 3. 비교 및 검증
        assertThat(savedCommentChild.preferences.size).isEqualTo(0)
        assertThat(savedPreference).isNull()
    }
    @Test
    @Transactional
    fun `대댓글 좋아요 +2`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        preferenceService.preferComment(createCommentChild.commentId, testMember1.id!!)
        preferenceService.preferComment(createCommentChild.commentId, testMember2.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.commentId)
        val savedPreference = preferenceRepository.findAll()
        // 3. 비교 및 검증
        assertThat(savedCommentChild!!.preferences.size).isEqualTo(2)
        assertThat(savedPreference.size).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `대댓글 삭제 시 좋아요 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        val beforeSavedCommentChild = postCommentRepository.findById(createCommentChild.commentId)
        preferenceService.preferComment(createCommentChild.commentId, testMember1.id!!)
        postCommentService.deletePostComment(createCommentChild.commentId, testMember1.id!!)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.commentId)
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
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment.commentId) ?: throw EntityNotFoundException()
        val savedPost = postRepository.findById(createPost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.post).isEqualTo(savedPost)
        assertThat(savedComment.writer).isEqualTo(testMember1)
        assertThat(savedComment.content).isEqualTo(postCommentRequest.content)
    }
    @Test
    @Transactional
    fun `대댓글 생성`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val parentComment = postCommentRepository.findById(createComment.commentId) ?: throw EntityNotFoundException()
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createCommentChild.commentId) ?: throw EntityNotFoundException()
        val savedPost = postRepository.findById(createPost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.parent).isEqualTo(parentComment)
        assertThat(savedComment.content).isEqualTo(postCommentChildRequest.content)
        assertThat(savedComment.post).isEqualTo(savedPost)
        assertThat(savedComment.writer).isEqualTo(testMember1)
    }
    @Test
    @Transactional
    fun `댓글 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val curTime = LocalDateTime.now()
        val createComment = postCommentService.createPostComment(
            postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val beforeComment = postCommentRepository.findById(createComment.commentId) ?: throw EntityNotFoundException()
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        val updateComment = postCommentService.updatePostComment(createComment.commentId, testMember1.id!!, updateCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(updateComment.commentId) ?: throw EntityNotFoundException()
        val savedPost = postRepository.findById(createPost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.id).isEqualTo(beforeComment.id)
        assertThat(savedComment.post).isEqualTo(savedPost)
        assertThat(savedComment.content).isEqualTo(updateCommentRequest.content)
        assertThat(savedComment.writer).isEqualTo(testMember1)
        assertThat(savedComment.parent).isEqualTo(beforeComment.parent)
        assertThat(savedComment.createdAt).isEqualTo(beforeComment.createdAt)
        assertThat(savedComment.updatedAt).isNotEqualTo(curTime)
    }
    @Test
    @Transactional
    fun `작성자가 아닌 멤버가 댓글 수정 접근`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(
            postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> { postCommentService.updatePostComment(createComment.commentId, testMember2.id!!, updateCommentRequest) }
    }
    @Test
    @Transactional
    fun `대댓글 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val parentComment = postCommentRepository.findById(createComment.commentId)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = createComment.commentId
        )
        val createCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        val updateCommentRequest = PostCommentUpdateRequest(
                content = "updateComment",
        )
        postCommentService.updatePostComment(createCommentChild.commentId, testMember1.id!!, updateCommentRequest)
        // 2. 실제 데이터
        val savedCommentChild = postCommentRepository.findById(createCommentChild.commentId) ?: throw EntityNotFoundException()
        val savedPost = postRepository.findById(createPost.postId) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedCommentChild.id).isEqualTo(createCommentChild.commentId)
        assertThat(savedCommentChild.post).isEqualTo(savedPost)
        assertThat(savedCommentChild.content).isEqualTo(updateCommentRequest.content)
        assertThat(savedCommentChild.writer).isEqualTo(testMember1)
        assertThat(savedCommentChild.parent).isEqualTo(parentComment)
        assertThat(savedCommentChild.updatedAt).isNotEqualTo(savedCommentChild.createdAt)
    }
    @Test
    @Transactional
    fun `댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment",
                parent = null,
        )
        val saveComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val deleteComment = postCommentService.deletePostComment(saveComment.commentId, testMember1.id!!)
        // 2. 비교 및 검증
        assertThat(postCommentRepository.findById(deleteComment.commentId)).isNull()
    }
    @Test
    @Transactional
    fun `작성자가 아닌 회원이 댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
            title = testPost.title,
            content = testPost.content,
            tagName = testPost.tag.tagName,
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
            content = "testComment",
            parent = null,
        )
        val saveComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        // 2. 비교 및 검증
        assertThrows<HandleAccessException> { postCommentService.deletePostComment(saveComment.commentId, testMember2.id!!) }
    }
    @Test
    @Transactional
    fun `대댓글 삭제`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tag.tagName
        )
        val createPost = postService.createPost(postCreationRequest, testMember1.id!!)
        val postCommentRequest = PostCommentCreationRequest(
                content = "testComment"
        )
        val saveComment = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                content = "testCommentChild",
                parent = saveComment.commentId
        )
        val saveCommentChild = postCommentService.createPostComment(postId = createPost.postId, memberId = testMember1.id!!, postCommentChildRequest)
        val deleteCommentChild = postCommentService.deletePostComment(saveCommentChild.commentId, testMember1.id!!)
        // 2. 비교 및 검증
        assertThat(postCommentRepository.findById(deleteCommentChild.commentId)).isNull()
    }
    @Test
    @Transactional
    fun `댓글 생성 후 post 상세 페이지 불러오기`(){
        // 1. 예상 데이터
        val postCreation = Post(
            title = "newPostTitle",
            content = "newPostContent",
            tag = postTag,
            writer = testMember1,
            viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        val postCommentRequest = PostCommentCreationRequest(
            content = "testComment"
        )
        val postComment1 = postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember1.id!!, postCommentRequest)
        postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember2.id!!, postCommentRequest)
        postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember1.id!!, postCommentRequest)
        //대댓글 2개 생성
        val postCommentChildRequest = PostCommentCreationRequest(
            content = "testCommentChild",
            parent = postComment1.commentId
        )
        postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember1.id!!, postCommentChildRequest)
        postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember3.id!!, postCommentChildRequest)
        //대댓글 생성 끝
        postCommentService.createPostComment(postId = createPost.id!!, memberId = testMember3.id!!, postCommentRequest)
        // 2. 실제 데이터
        val getPostSingle = postService.getPostDetail(createPost.id!!, testMember1.id!!)
        // 3. 비교 및 검증
        assertThat(getPostSingle.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.tagName).isEqualTo(postCreation.tag.tagName)
        assertThat(getPostSingle.writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getPostSingle.commentCount).isEqualTo(6)
        assertThat(getPostSingle.viewCount).isEqualTo(1)
        assertThat(getPostSingle.commentList[0].writerNickname).isEqualTo("글쓴이")
        assertThat(getPostSingle.commentList[1].writerNickname).isEqualTo("익명1")
        assertThat(getPostSingle.commentList[2].writerNickname).isEqualTo("글쓴이")
        assertThat(getPostSingle.commentList[3].writerNickname).isEqualTo("익명2")
        assertThat(getPostSingle.commentList[0].childComments.size).isEqualTo(2)
        assertThat(getPostSingle.commentList[0].childComments[0].writerNickname).isEqualTo("글쓴이")
        assertThat(getPostSingle.commentList[0].childComments[1].writerNickname).isEqualTo("익명2")
    }
    @Test
    @Transactional
    fun `tag별 게시글 list 가져오기`(){
        // 1. 예상 데이터
        val postTagCreation = PostTag(
            tagName = "diffTag",
        )
        postTagRepository.save(postTagCreation)
        val postCreationRequest = PostCreationRequest(
            title = "newPostTitle",
            content = "newPostContent",
            tagName = "testTag",
        )
        postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)
        val diffPostCreationRequest = PostCreationRequest(
            title = "diffPostTitle",
            content = "diffPostContent",
            tagName = "diffTag",
        )
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        // 2. 실제 데이터
        val getDiffPostList = postService.getPostListByTag(postTagCreation.tagName, 0)
        val getTestPostList = postService.getPostListByTag(postTag.tagName, 0)
        val getPostList = postService.getPostList(0)
        // 3. 비교 및 검증
        assertThat(getPostList.content.size).isEqualTo(6)
        assertThat(getDiffPostList.content.size).isEqualTo(3)
        assertThat(getDiffPostList.content[0].title).isEqualTo(diffPostCreationRequest.title)
        assertThat(getDiffPostList.content[0].content).isEqualTo(diffPostCreationRequest.content)
        assertThat(getDiffPostList.content[0].tagName).isEqualTo(diffPostCreationRequest.tagName)
        assertThat(getDiffPostList.content[0].writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getDiffPostList.content[0].commentCount).isEqualTo(0)
        assertThat(getDiffPostList.content[0].viewCount).isEqualTo(0)
        assertThat(getTestPostList.content.size).isEqualTo(3)
        assertThat(getTestPostList.content[0].title).isEqualTo(postCreationRequest.title)
        assertThat(getTestPostList.content[0].content).isEqualTo(postCreationRequest.content)
        assertThat(getTestPostList.content[0].tagName).isEqualTo(postCreationRequest.tagName)
        assertThat(getTestPostList.content[0].writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(getTestPostList.content[0].commentCount).isEqualTo(0)
        assertThat(getTestPostList.content[0].viewCount).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `tag별 게시글 목록 가져오기 삭제 글 예외로 가져오기`(){
        // 1. 예상 데이터
        val postTagCreation = PostTag(
            tagName = "diffTag",
        )
        postTagRepository.save(postTagCreation)
        val postCreationRequest = PostCreationRequest(
            title = "newPostTitle",
            content = "newPostContent",
            tagName = "testTag",
        )
        val savedPost = postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)

        postService.deletePost(savedPost.postId, testMember1.id!!)

        val diffPostCreationRequest = PostCreationRequest(
            title = "diffPostTitle",
            content = "diffPostContent",
            tagName = "diffTag",
        )
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        // 2. 실제 데이터
        val getDiffPostList = postService.getPostListByTag(postTagCreation.tagName, 0)
        val getTestPostList = postService.getPostListByTag(postTag.tagName, 0)
        val getPostList = postService.getPostList(0)
        // 3. 비교 및 검증
        assertThat(getPostList.content.size).isEqualTo(5)
        assertThat(getDiffPostList.content.size).isEqualTo(3)
        assertThat(getTestPostList.content.size).isEqualTo(2)
    }
    @Test
    @Transactional
    fun `검색 기능`(){
        // 1. 예상 데이터
        val postTagCreation = PostTag(
            tagName = "diffTag",
        )
        postTagRepository.save(postTagCreation)
        val postCreationRequest = PostCreationRequest(
            title = "newPostTitle",
            content = "newPostContent",
            tagName = "testTag",
        )
        postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)
        postService.createPost(postCreationRequest, testMember1.id!!)
        val diffPostCreationRequest = PostCreationRequest(
            title = "diffPostTitle",
            content = "diffPostContent",
            tagName = "diffTag",
        )
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        postService.createPost(diffPostCreationRequest, testMember1.id!!)
        val uniquePostCreationRequest = PostCreationRequest(
            title = "uniquePostTitle",
            content = "diffPostContent",
            tagName = "diffTag",
        )
        postService.createPost(uniquePostCreationRequest, testMember1.id!!)
        // 2. 실제 데이터
        val searchPost = postService.searchPost("unique", 0)
        // 3. 비교 및 검증
        assertThat(searchPost.content.size).isEqualTo(1)
        assertThat(searchPost.content[0].title).isEqualTo(uniquePostCreationRequest.title)
        assertThat(searchPost.content[0].content).isEqualTo(uniquePostCreationRequest.content)
        assertThat(searchPost.content[0].tagName).isEqualTo(uniquePostCreationRequest.tagName)
        assertThat(searchPost.content[0].writerSchoolName).isEqualTo(univService.getSchoolNameByEmailDomain(testMember1.email))
        assertThat(searchPost.content[0].commentCount).isEqualTo(0)
        assertThat(searchPost.content[0].viewCount).isEqualTo(0)
    }
}
