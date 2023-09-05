package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostRepository : JpaRepository<Post, Long>, PagingAndSortingRepository<Post, Long>{
    fun findById(id: UUID?): Post?
    fun save(postUpdateRequest: PostUpdateRequest): UUID
    override fun findAll(pageable: Pageable): Page<Post>
    fun findAllByTagId(tagId: PostTag, pageable: Pageable): Page<Post>
}