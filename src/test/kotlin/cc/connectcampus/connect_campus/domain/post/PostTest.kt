package cc.connectcampus.connect_campus.domain.post

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.exception.PostContentInvalidException
import cc.connectcampus.connect_campus.domain.post.exception.PostTagInvalidException
import cc.connectcampus.connect_campus.domain.post.exception.PostTitleInvalidException
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostLikeRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostTagRepository
import cc.connectcampus.connect_campus.domain.post.service.PostCommentService0
import cc.connectcampus.connect_campus.domain.post.service.PostLikeService0
import cc.connectcampus.connect_campus.domain.post.service.PostServiceV0
import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.springframework.transaction.annotation.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class PostTest (
        @Autowired val postRepository: PostRepository,
        @Autowired val postTagRepository: PostTagRepository,
        @Autowired val postService: PostServiceV0,
        @Autowired val postLikeService: PostLikeService0,
        @Autowired val memberRepository: MemberRepository,
        @Autowired val postLikeRepository: PostLikeRepository,
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
        testMember2 = Member.fixture()
        testPost = Post.fixture()
        postTag = PostTag.fixture()
        postUpdateTag = PostTag(
                tagName = "updateTag",
        )
        postTagRepository.save(postTag)
        postTagRepository.save(postUpdateTag)
        memberRepository.save(testMember1)
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
                writerId = testMember1,
        )
        val createPost = postService.create(postCreationRequest)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(createPost.post.id) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.title).isEqualTo(postCreationRequest.title)
        assertThat(savedPost.content).isEqualTo(postCreationRequest.content)
        assertThat(savedPost.tagId.tagName).isEqualTo(postCreationRequest.tagName)
        assertThat(savedPost.writerId).isEqualTo(postCreationRequest.writerId)
    }
    @Test
    @Transactional
    fun `post 생성 title 필터 예외`() {
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "씨발",
                content = "newPostContent",
                tagName = "testTag",
                writerId = testMember1,
        )
        // 2. 검증
        assertThrows<PostTitleInvalidException> {
            postService.create(postCreationRequest)
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
                writerId = testMember1,
        )
        // 2. 검증
        assertThrows<PostContentInvalidException> {
            postService.create(postCreationRequest)
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
                writerId = testMember1,
        )
        // 2. 검증
        assertThrows<PostTagInvalidException> {
            postService.create(postCreationRequest)
        }
    }
    //post 전체 리스트 불러오기(10~15개)
    @Test
    @Transactional
    fun `post 상세 페이지 불러오기`(){
        // 1. 예상 데이터
        val postCreation = Post(
                title = "newPostTitle",
                content = "newPostContent",
                tagId = postTag,
                writerId = testMember1,
                likeCount = 0,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        // 2. 실제 데이터
        val getPostSingle = postService.readSingle(createPost.id!!, testMember1)
        // 3. 비교 및 검증
        assertThat(getPostSingle.postDetail.title).isEqualTo(postCreation.title)
        assertThat(getPostSingle.postDetail.content).isEqualTo(postCreation.content)
        assertThat(getPostSingle.postDetail.tagId.tagName).isEqualTo(postCreation.tagId.tagName)
        assertThat(getPostSingle.postDetail.writerId).isEqualTo(postCreation.writerId)
        assertThat(getPostSingle.postDetail.viewCount).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `post 수정`(
    ){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
                writerId = testMember1
        )
        val curTime: LocalDateTime = LocalDateTime.now()
        val createPost = postService.create(postCreationRequest)
        val postUpdateRequest = PostUpdateRequest(
                id = createPost.post.id!!,
                title = "updateTitle",
                content = "updateContent",
                tagName = "updateTag",
                writerId = testMember1,
        )
        postService.update(postUpdateRequest)
        // 2. 실제 데이터
        val savedPost = postRepository.findById(postUpdateRequest.id) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedPost.content).isEqualTo(postUpdateRequest.content)
        assertThat(savedPost.title).isEqualTo(postUpdateRequest.title)
        assertThat(savedPost.tagId.tagName).isEqualTo(postUpdateRequest.tagName)
        assertThat(savedPost.writerId).isEqualTo(postUpdateRequest.writerId)
        assertThat(savedPost.createdAt).isEqualTo(postRepository.findById(createPost.post.id)!!.createdAt)
        assertThat(savedPost.updatedAt).isNotEqualTo(curTime)
        assertThat(savedPost.likeCount).isEqualTo(0)
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
                likeCount = 0,
                viewCount = 0,
        )
        val createPost = postRepository.save(postCreation)
        val deletePost = postService.delete(
                PostDeletionRequest(
                        id = createPost.id!!,
                        writerId = postRepository.findById(createPost.id)!!.writerId,
                        )
        )
        // 2. 비교 및 검증
        assertThat(deletePost).isEqualTo(true)
        val check = postRepository.findById(createPost.id)
        assertThat(check).isNull()
    }
    @Test
    @Transactional
    fun `좋아요 +1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
                writerId = testMember1,
        )
        val createPost = postService.create(postCreationRequest)
        postLikeService.postLikeManage(
                PostLikeRequest(
                        post = postRepository.findById(createPost.post.id)!!,
                        user = memberRepository.findById(testMember1.id!!) ?: throw EntityNotFoundException(),
                )
        )
        // 2. 비교 및 검증
        assertThat(postRepository.findById(createPost.post.id)!!.likeCount).isEqualTo(1)
        assertThat(postLikeRepository.findAll().count()).isEqualTo(1)
    }
    @Test
    @Transactional
    fun `좋아요 -1`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = "newPostTitle",
                content = "newPostContent",
                tagName = "testTag",
                writerId = testMember1,
        )
        val createPost = postService.create(postCreationRequest)
        // 좋아요 +1
        postLikeService.postLikeManage(
                PostLikeRequest(
                        post = postRepository.findById(createPost.post.id)!!,
                        user = memberRepository.findById(testMember1.id!!) ?: throw EntityNotFoundException(),
                )
        )
        // 좋아요 -1
        postLikeService.postLikeManage(
                PostLikeRequest(
                        post = postRepository.findById(createPost.post.id)!!,
                        user = memberRepository.findById(testMember1.id!!) ?: throw EntityNotFoundException(),
                )
        )
        // 2. 비교 및 검증
        assertThat(postRepository.findById(createPost.post.id)!!.likeCount).isEqualTo(0)
        assertThat(postLikeRepository.findAll().count()).isEqualTo(0)
    }
    @Test
    @Transactional
    fun `댓글 생성`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
                writerId = testMember1
        )
        val createPost = postService.create(postCreationRequest)
        val postCommentRequest = PostCommentCreationRequest(
                post = postRepository.findById(createPost.post.id)!!,
                writerId = testMember1,
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createComment) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.post).isEqualTo(postCommentRequest.post)
        assertThat(savedComment.writerId).isEqualTo(postCommentRequest.writerId)
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
                writerId = testMember1
        )
        val createPost = postService.create(postCreationRequest)
        val postCommentRequest = PostCommentCreationRequest(
                post = postRepository.findById(createPost.post.id)!!,
                writerId = testMember1,
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentService.postCommentCreate(postCommentRequest)
        val postCommentChildRequest = PostCommentCreationRequest(
                post = postRepository.findById(createPost.post.id)!!,
                writerId = testMember1,
                content = "testCommentChild",
                parent = postCommentRepository.findById(createComment)
        )
        val createCommentChild = postCommentService.postCommentCreate(postCommentChildRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(createCommentChild) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.parentId).isEqualTo(postCommentChildRequest.parent)
    }
    @Test
    @Transactional
    fun `댓글 수정`(){
        // 1. 예상 데이터
        val postCreationRequest = PostCreationRequest(
                title = testPost.title,
                content = testPost.content,
                tagName = testPost.tagId.tagName,
                writerId = testMember1
        )
        val createPost = postService.create(postCreationRequest)
        val postCommentRequest = PostCommentCreationRequest(
                post = postRepository.findById(createPost.post.id)!!,
                writerId = testMember1,
                content = "testComment",
                parent = null,
        )
        val curTime = LocalDateTime.now()
        val createComment = postCommentRepository.findById(postCommentService.postCommentCreate(postCommentRequest)) ?: throw EntityNotFoundException()
        val updateCommentRequest = PostCommentUpdateRequest(
                id = createComment.id!!,
                writerId = createComment.writerId,
                postId = createComment.post,
                content = "updateComment",
        )
        val updateComment = postCommentService.postCommentUpdate(updateCommentRequest)
        // 2. 실제 데이터
        val savedComment = postCommentRepository.findById(updateComment) ?: throw EntityNotFoundException()
        // 3. 비교 및 검증
        assertThat(savedComment.content).isEqualTo(updateCommentRequest.content)
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
                writerId = testMember1
        )
        val createPost = postService.create(postCreationRequest)
        val postCommentRequest = PostCommentCreationRequest(
                post = postRepository.findById(createPost.post.id)!!,
                writerId = testMember1,
                content = "testComment",
                parent = null,
        )
        val createComment = postCommentRepository.findById(postCommentService.postCommentCreate(postCommentRequest)) ?: throw EntityNotFoundException()
        val deleteCommentRequest = PostCommentDeletionRequest(
                id = createComment.id!!,
                writerId = testMember1,
        )
        val deleteComment = postCommentService.postCommentDeletion(deleteCommentRequest)
        // 2. 비교 및 검증
        assertThat(postCommentRepository.findById(deleteComment)).isNull()
    }
}
