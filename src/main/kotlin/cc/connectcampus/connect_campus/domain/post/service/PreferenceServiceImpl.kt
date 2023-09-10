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
class PreferenceServiceImpl(
    val postRepository: PostRepository,
    val preferenceRepository: PreferenceRepository,
    val memberRepository: MemberRepository,
    val postCommentRepository: PostCommentRepository,
) {
    @Transactional
    fun preferPost(postId: UUID, memberId: UUID): Int {
        val savedPost = postRepository.findById(postId) ?: throw EntityNotFoundException()
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()

        val savedPreference = preferenceRepository.findByPostAndMember(savedPost, savedMember)

        if (savedPreference != null) {
            preferenceRepository.delete(savedPreference)
            savedPost.preferences.removeIf { it.id == savedPreference.id }
        } else {
            val newPreference = Preference(post = savedPost, member = savedMember)
            preferenceRepository.save(newPreference)
            savedPost.preferences.add(newPreference)
        }

        return savedPost.preferences.size
    }

    @Transactional
    fun preferComment(commentId: UUID, memberId: UUID): Int {
        val savedComment = postCommentRepository.findById(commentId) ?: throw EntityNotFoundException()
        val savedMember = memberRepository.findById(memberId) ?: throw EntityNotFoundException()

        val savedPreference = preferenceRepository.findByCommentAndMember(savedComment, savedMember)

        if (savedPreference != null) {
            preferenceRepository.delete(savedPreference)
            savedComment.preferences.removeIf { it.id == savedPreference.id }
        } else {
            val newPreference = Preference(comment = savedComment, member = savedMember)
            preferenceRepository.save(newPreference)
            savedComment.preferences.add(newPreference)
        }

        return savedComment.preferences.size
    }
}