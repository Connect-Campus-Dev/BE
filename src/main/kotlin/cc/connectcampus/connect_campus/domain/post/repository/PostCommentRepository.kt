package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostCommentRepository : JpaRepository<PostComment, Long> {
    fun findById(id: UUID) : PostComment?
    fun findAllByPost(post: Post) : MutableList<PostComment>
}