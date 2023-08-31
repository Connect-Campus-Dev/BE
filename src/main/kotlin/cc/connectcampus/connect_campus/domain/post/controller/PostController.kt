package cc.connectcampus.connect_campus.domain.post.controller

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.dto.response.*
import cc.connectcampus.connect_campus.domain.post.service.PostCommentService0
import cc.connectcampus.connect_campus.domain.post.service.PostLikeService0
import cc.connectcampus.connect_campus.domain.post.service.PostServiceV0
import cc.connectcampus.connect_campus.global.config.security.InvalidTokenException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/community")
class PostController (
    val postService: PostServiceV0,
    val postLikeService: PostLikeService0,
    val postCommentService: PostCommentService0,
){
    @PostMapping("/post")
    fun createPost(@RequestBody postCreationRequest: PostCreationRequest, authentication: Authentication) : PostDetailResponse{
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        return postService.create(postCreationRequest, memberId)
    }
    @PostMapping("/{id}/like")
    fun postLike(postLikeRequest: PostLikeRequest) : Int{
        return postLikeService.postLikeManage(postLikeRequest)
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
    @GetMapping("/{id}")
    fun getSinglePost(@RequestParam id: UUID, viewMember: Member) : PostResponse{
        return postService.readSingle(id, viewMember)
    }
    @PutMapping("/post/{id}")
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
    @DeleteMapping("/post/{id}")
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