package cc.connectcampus.connect_campus.domain.post.controller

import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.dto.response.*
import cc.connectcampus.connect_campus.domain.post.service.PostCommentServiceImpl
import cc.connectcampus.connect_campus.domain.post.service.PreferenceServiceImpl
import cc.connectcampus.connect_campus.domain.post.service.PostServiceImpl
import cc.connectcampus.connect_campus.global.config.security.InvalidTokenException
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/community")
class PostController (
    val postService: PostServiceImpl,
    val preferenceService : PreferenceServiceImpl,
    val postCommentService: PostCommentServiceImpl,
){
    @PostMapping("/post")
    fun createPost(@RequestBody postCreationRequest: PostCreationRequest, authentication: Authentication) : PostResponse{
        val memberId: UUID = getMemberId(authentication)
        return postService.createPost(postCreationRequest, memberId)
    }

    @PostMapping("/post/{postId}/preference")
    fun preferPost(@PathVariable postId: UUID, authentication: Authentication) : Int{
        val memberId: UUID = getMemberId(authentication)
        return preferenceService.preferPost(postId, memberId)
    }
    @PostMapping("/comment/{commentId}/preference")
    fun preferPostComment(@PathVariable commentId: UUID, authentication: Authentication) : Int{
        val memberId: UUID = getMemberId(authentication)
        return preferenceService.preferComment(commentId, memberId)
    }
    @PostMapping("/post/{postId}/comment")
    fun createPostComment(@PathVariable postId: UUID, @RequestBody postCommentCreationRequest: PostCommentCreationRequest,
                          authentication: Authentication) : PostCommentResponse{
        val memberId: UUID = getMemberId(authentication)
        return postCommentService.createPostComment(postId, memberId, postCommentCreationRequest)
    }
    @PostMapping("/post/comment-child")
    fun createPostCommentChild(@RequestParam("postId") postId: UUID, @RequestParam("commentId") commentId: String,
                               @RequestBody postCommentCreationRequest: PostCommentCreationRequest, authentication: Authentication) : PostCommentResponse{
        val memberId: UUID = getMemberId(authentication)
        val commentUUID = UUID.fromString(commentId)
        postCommentCreationRequest.parent = commentUUID
        return postCommentService.createPostComment(postId, memberId, postCommentCreationRequest)
    }
    @GetMapping
    fun getListPost(@RequestParam("page") page: Int) : Page<PostResponse>{
        return postService.getPostList(page)
    }
    @GetMapping("/{tagName}")
    fun getListPostByTag(@PathVariable tagName: String, @RequestParam("page") page: Int) : Page<PostResponse>{
        return postService.getPostListByTag(tagName, page)
    }
    @GetMapping("/post/{postId}")
    fun getSinglePost(@PathVariable postId: UUID, authentication: Authentication) : PostResponse{
        val memberId: UUID = getMemberId(authentication)
        return postService.getPostDetail(postId, memberId)
    }
    @GetMapping("/post/{keyword}")
    fun searchPost(@PathVariable keyword: String, @RequestParam("page") page: Int) : Page<PostResponse>{
        return postService.searchPost(keyword, page)
    }
    @PutMapping("/post/{postId}")
    fun updatePost(@PathVariable postId: UUID, @RequestBody postUpdateRequest: PostUpdateRequest, authentication: Authentication) : PostResponse{
        val memberId: UUID = getMemberId(authentication)
        return postService.updatePost(postId, postUpdateRequest, memberId)
    }
    @PutMapping("/comment/{commentId}")
    fun updatePostComment(@PathVariable commentId: UUID, @RequestBody postCommentUpdateRequest: PostCommentUpdateRequest,
                          authentication: Authentication) : PostCommentResponse{
        val memberId: UUID = getMemberId(authentication)
        return postCommentService.updatePostComment(commentId, memberId, postCommentUpdateRequest)
    }
    @DeleteMapping("/post/{postId}")
    fun deletePost(@PathVariable postId: UUID, authentication: Authentication) : PostResponse{
        val memberId: UUID = getMemberId(authentication)
        return postService.deletePost(postId, memberId)
    }
    @DeleteMapping("/comment/{commentId}")
    fun deletePostComment(@PathVariable commentId: UUID, authentication: Authentication) : PostCommentResponse{
        val memberId: UUID = getMemberId(authentication)
        return postCommentService.deletePostComment(commentId, memberId)
    }

    private fun getMemberId(authentication: Authentication): UUID {
        return (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
    }
}