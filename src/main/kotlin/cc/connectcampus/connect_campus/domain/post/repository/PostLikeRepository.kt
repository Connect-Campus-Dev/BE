package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLikeRepository : JpaRepository<PostLike, Long>{
    fun existsByPostAndMember(post: Post, member: Member) : Boolean
    fun findByPostAndMember(post: Post, member: Member) : PostLike
}