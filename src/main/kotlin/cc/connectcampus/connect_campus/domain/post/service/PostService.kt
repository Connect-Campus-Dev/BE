package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostDetailResponse
import cc.connectcampus.connect_campus.domain.post.dto.response.PostResponse
import org.springframework.data.domain.Page
import java.util.*

interface PostService {
    fun create(postCreationRequest: PostCreationRequest, memberId: UUID) : PostDetailResponse
    fun readList(page: Int) : Page<Post>
    fun readSingle(postId: UUID, memberId: UUID?) : PostResponse
    fun update(postId: UUID, postUpdateRequest: PostUpdateRequest, memberId: UUID) : PostDetailResponse
    fun delete(postId: UUID, memberId: UUID) : Post

}