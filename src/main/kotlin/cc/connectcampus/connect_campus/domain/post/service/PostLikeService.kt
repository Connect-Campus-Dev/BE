package cc.connectcampus.connect_campus.domain.post.service

import cc.connectcampus.connect_campus.domain.post.dto.request.PostLikeRequest

interface PostLikeService {
    fun postLikeManage(postLikeRequest: PostLikeRequest) : Int
}