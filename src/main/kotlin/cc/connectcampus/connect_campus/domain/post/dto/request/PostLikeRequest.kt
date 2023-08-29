package cc.connectcampus.connect_campus.domain.post.dto.request

import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.post.domain.Post
import jakarta.validation.Valid

data class PostLikeRequest (
    @field:Valid
    val post: Post,
    @field:Valid
    val user: Member,
        )