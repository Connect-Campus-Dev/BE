package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.domain.PostLike
import cc.connectcampus.connect_campus.domain.post.dto.request.PostLikeRequest
import cc.connectcampus.connect_campus.domain.post.repository.PostLikeRepository
import cc.connectcampus.connect_campus.domain.post.repository.PostRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostLikeService0 (
        val postRepository: PostRepository,
        val postLikeRepository: PostLikeRepository,
): PostLikeService{
    @Transactional
    override fun postLikeManage(postLikeRequest: PostLikeRequest): Int {
        //게시글 검증
        val savedPost = postRepository.findById(postLikeRequest.post.id) ?: throw EntityNotFoundException()
        //게시글 좋아요 유저 검증
        if (postLikeRepository.existsByPostAndMember(postLikeRequest.post, postLikeRequest.user)) {
            savedPost.likeCount--
            val savedLike = postLikeRepository.findByPostAndMember(postLikeRequest.post, postLikeRequest.user)
            postRepository.save(savedPost)
            postLikeRepository.delete(savedLike)
        } else {
            savedPost.likeCount++
            postLikeRepository.save(PostLike(post = postLikeRequest.post, member = postLikeRequest.user))
            postRepository.save(savedPost)
        }
        val likeCountTmp = postRepository.findById(savedPost.id) ?: throw EntityNotFoundException()
        return likeCountTmp.likeCount
    }
}