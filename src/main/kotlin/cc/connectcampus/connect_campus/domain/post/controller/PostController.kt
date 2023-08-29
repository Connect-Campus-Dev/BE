package cc.connectcampus.connect_campus.domain.post.controller

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.dto.request.*
import cc.connectcampus.connect_campus.domain.post.dto.response.*
import cc.connectcampus.connect_campus.domain.post.service.PostCommentService0
import cc.connectcampus.connect_campus.domain.post.service.PostLikeService0
import cc.connectcampus.connect_campus.domain.post.service.PostServiceV0
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/post")
class PostController (
    val postService: PostServiceV0,
    val postLikeService: PostLikeService0,
    val postCommentService: PostCommentService0,
){
    @PostMapping
    fun createPost(@RequestBody postCreationRequest: PostCreationRequest) : PostDetailResponse{
        return postService.create(postCreationRequest)
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
    fun getListPost() : List<Post>{
        return postService.readAll()
    }
    @GetMapping("/{id}")
    fun getSinglePost(@RequestParam id: UUID, viewMember: Member) : PostResponse{
        return postService.readSingle(id, viewMember)
    }
    @PutMapping
    fun updatePost(@RequestBody postUpdateRequest: PostUpdateRequest) : UUID{
        return postService.update(postUpdateRequest)
    }
    @PutMapping("/comment")
    fun updatePostComment(postCommentUpdateRequest: PostCommentUpdateRequest) : UUID{
        return postCommentService.postCommentUpdate(postCommentUpdateRequest)
    }
    @PutMapping("/comment/{id}")
    fun updatePostCommentChild(postCommentUpdateRequest: PostCommentUpdateRequest) : UUID{
        return postCommentService.postCommentUpdate(postCommentUpdateRequest)
    }
    @DeleteMapping
    fun deletePost(@Valid @RequestBody postDeletionRequest: PostDeletionRequest) : Boolean{
        return postService.delete(postDeletionRequest)
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