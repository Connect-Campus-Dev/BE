package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostRepository : JpaRepository<Post, Long>{
    fun findById(id: UUID?): Post?
    fun save(postUpdateRequest: PostUpdateRequest): UUID
}