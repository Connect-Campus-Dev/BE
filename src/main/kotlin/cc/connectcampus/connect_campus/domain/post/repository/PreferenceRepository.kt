package cc.connectcampus.connect_campus.domain.post.repository

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import cc.connectcampus.connect_campus.domain.post.domain.Preference
import cc.connectcampus.connect_campus.domain.post.domain.PostComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PreferenceRepository : JpaRepository<Preference, Long>{
    fun existsByPostAndMember(post: Post, member: Member) : Boolean
    fun findByPostAndMember(post: Post, member: Member) : Preference?
    fun existsByCommentAndMember(comment: PostComment, member: Member) : Boolean
    fun findByCommentAndMember(comment: PostComment, member: Member) : Preference?
}