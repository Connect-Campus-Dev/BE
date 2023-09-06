package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import cc.connectcampus.connect_campus.domain.post.dto.request.PostCreationRequest
import cc.connectcampus.connect_campus.domain.post.dto.response.PostResponse
import org.springframework.data.domain.Page
import java.util.*

interface PostService {
    fun create(postCreationRequest: PostCreationRequest, memberId: UUID) : PostResponse
    fun readList(page: Int) : Page<PostResponse>
    fun readSingle(postId: UUID, memberId: UUID?) : PostResponse
    fun update(postId: UUID, postUpdateRequest: PostUpdateRequest, memberId: UUID) : PostResponse
    fun delete(postId: UUID, memberId: UUID) : PostResponse
    fun getListByTag(tagUUID: UUID, page: Int): Page<PostResponse>
    fun searchPost(searchWord: String?, page: Int): Page<PostResponse>
}