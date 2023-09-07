package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import cc.connectcampus.connect_campus.domain.post.dto.request.PostUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PostRepository : JpaRepository<Post, Long>{
    fun findById(id: UUID?): Post?
    fun save(postUpdateRequest: PostUpdateRequest): UUID
    fun findAllByIsDeletedFalse(pageable: Pageable): Page<Post>
    fun findAllByTagAndIsDeletedFalse(tag: PostTag, pageable: Pageable): Page<Post>

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% order by p.createdAt desc")
    fun searchByTitleAndContentContaining(keyword: String, pageable: Pageable): Page<Post>

//    @Query(value = "SELECT * FROM post WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE)", nativeQuery = true)
//    fun searchPost(keyword: String, pageable: Pageable): Page<Post>
}