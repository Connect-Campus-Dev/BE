package cc.connectcampus.connect_campus.domain.post.controller

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.dto.response.*
import cc.connectcampus.connect_campus.domain.post.service.PostCommentService0
import cc.connectcampus.connect_campus.domain.post.service.PreferenceService0
import cc.connectcampus.connect_campus.domain.post.service.PostServiceV0
import cc.connectcampus.connect_campus.global.config.security.InvalidTokenException
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/community")
class PostController (
        val postService: PostServiceV0,
        val preferenceService : PreferenceService0,
        val postCommentService: PostCommentService0,
){
    @PostMapping("/post")
    fun createPost(@RequestBody postCreationRequest: PostCreationRequest, authentication: Authentication) : PostDetailResponse{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        return postService.create(postCreationRequest, memberId)
    }
    @PostMapping("/post/preference/{postId}")
    fun postPreference(@PathVariable postId: String, authentication: Authentication) : Int{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        val postUUID = UUID.fromString(postId)
        return preferenceService.postPreferenceManage(postUUID, memberId)
    }
    @PostMapping("/comment/preference/{commentId}")
    fun commentPreference(@PathVariable commentId: String, authentication: Authentication) : Int{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        val commentUUID = UUID.fromString(commentId)
        return preferenceService.commentPreferenceManage(commentUUID, memberId)
    }
    @PostMapping("/comment")
    fun createPostComment(postCommentCreationRequest: PostCommentCreationRequest) : UUID{
        return postCommentService.postCommentCreate(postCommentCreationRequest)
    }
    @PostMapping("/comment/{id}")
    fun createPostCommentChild(postCommentCreationRequest: PostCommentCreationRequest) : UUID{
        return postCommentService.postCommentCreate(postCommentCreationRequest)
    }
    @GetMapping
    fun getListPost(@RequestParam("page") page: Int) : Page<Post>{
        return postService.readList(page)
    }
    @GetMapping("/post/{postId}")
    fun getSinglePost(@PathVariable postId: String, authentication: Authentication) : PostResponse{
        //로그인 안한 멤버가 페이지 확인할 경우 조회수 count를 안하기 위해 memberId null로 반환
        val memberId: UUID? = (authentication.principal as? CustomUser)?.id
        val postUUID = UUID.fromString(postId)
        return postService.readSingle(postUUID, memberId)
    }
    @PutMapping("/post/{postId}")
    fun updatePost(@PathVariable postId: String, @RequestBody postUpdateRequest: PostUpdateRequest, authentication: Authentication) : PostDetailResponse{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        val postUUID = UUID.fromString(postId)
        return postService.update(postUUID, postUpdateRequest, memberId)
    }
    @PutMapping("/comment")
    fun updatePostComment(postCommentUpdateRequest: PostCommentUpdateRequest) : UUID{
        return postCommentService.postCommentUpdate(postCommentUpdateRequest)
    }
    @PutMapping("/comment/{id}")
    fun updatePostCommentChild(postCommentUpdateRequest: PostCommentUpdateRequest) : UUID{
        return postCommentService.postCommentUpdate(postCommentUpdateRequest)
    }
    @DeleteMapping("/post/{postId}")
    fun deletePost(@PathVariable postId: String, authentication: Authentication) : Post{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        val postUUID = UUID.fromString(postId)
        return postService.delete(postUUID, memberId)
    }
    @DeleteMapping("/comment")
    fun deletePostComment(postCommentDeletionRequest: PostCommentDeletionRequest) : UUID{
        return postCommentService.postCommentDeletion(postCommentDeletionRequest)
    }
    @DeleteMapping("/comment/{id}")
    fun deletePostCommentChild(postCommentDeletionRequest: PostCommentDeletionRequest) : UUID{
        return postCommentService.postCommentDeletion(postCommentDeletionRequest)
    }



}