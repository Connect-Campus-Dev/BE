package cc.connectcampus.connect_campus.domain.post.service

import java.util.*

interface PreferenceService {
    fun postPreferenceManage(postId: UUID, memberId: UUID) : Int
    fun commentPreferenceManage(commentId: UUID, memberId: UUID) : Int
}