package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.post.domain.Preference
import cc.connectcampus.connect_campus.domain.post.repository.PostCommentRepository
import cc.connectcampus.connect_campus.domain.post.repository.PreferenceRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PreferenceService0 (
        val postRepository: PostRepository,
        val preferenceRepository: PreferenceRepository,
        val memberRepository: MemberRepository,
        val postCommentRepository: PostCommentRepository,
): PreferenceService{
    @Transactional
    override fun postPreferenceManage(postId: UUID, memberId: UUID): Int {
        //게시글 검증
        val savedPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        //멤버 호출
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()
        //게시글 좋아요 유저 검증
        if (preferenceRepository.existsByPostAndMember(savedPost, savedMember)) {
            val savedPreference = preferenceRepository.findByPostAndMember(savedPost, savedMember)!!
            preferenceRepository.delete(savedPreference)
            savedPost.preferences!!.removeIf { it.id == savedPreference.id }
            postRepository.save(savedPost)
        } else {
            val savePreference = preferenceRepository.save(Preference(post = savedPost, member = savedMember))
            savedPost.preferences!!.add(savePreference)
            postRepository.save(savedPost)
        }
        val resultPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        return resultPost.preferences!!.size
    }

    @Transactional
    override fun commentPreferenceManage(commentId: UUID, memberId: UUID): Int {
        //댓글 검증
        val savedComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        //멤버 호출
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()
        //댓글 좋아요 유저 검증
        if (preferenceRepository.existsByCommentAndMember(savedComment, savedMember)){
            val savedPreference = preferenceRepository.findByCommentAndMember(savedComment, savedMember)
            preferenceRepository.delete(savedPreference!!)
            savedComment.preferences!!.removeIf{ it.id == savedPreference.id}
            postCommentRepository.save(savedComment)
        } else {
            val savePreference = preferenceRepository.save(Preference(comment = savedComment, member = savedMember))
            savedComment.preferences!!.add(savePreference)
            postCommentRepository.save(savedComment)
        }
        val resultComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        return resultComment.preferences!!.size
    }
}