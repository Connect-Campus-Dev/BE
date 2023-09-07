package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.post.domain.PostTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostTagRepository : JpaRepository<PostTag, Long> {
    fun findByTagName(tagName: String): PostTag?
    fun findById(id: UUID): PostTag?
}