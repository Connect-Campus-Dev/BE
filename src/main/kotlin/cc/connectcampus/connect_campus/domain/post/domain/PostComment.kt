package cc.connectcampus.connect_campus.domain.post.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class PostComment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "post_id")
    var post: Post,

    @ManyToOne
    @JoinColumn(name = "writer_id")
    val writer: Member,

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: PostComment? = null,

    @Column(nullable = false)
    var content: String,

    @OneToMany(mappedBy = "comment")
    val preferences: MutableList<Preference> = mutableListOf(),

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column
    var isDeleted: Boolean = false,
) {
    companion object {
        fun fixture(
            id: UUID? = null,
            post: Post = Post.fixture(),
            member: Member = Member.fixture(),
            parent: PostComment? = null,
            content: String = "testComment",
            preferences: MutableList<Preference> = mutableListOf(),
            createdAt: LocalDateTime = LocalDateTime.now()
        ): PostComment {
            return PostComment(id, post, member, parent, content, preferences, createdAt)
        }
    }
}