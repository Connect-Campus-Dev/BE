package cc.connectcampus.connect_campus.domain.post.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
class PostLike (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?= null,

    @ManyToOne
    @JoinColumn(name =  "post_id")
    val post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val member: Member,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime? = null,
)