package cc.connectcampus.connect_campus.domain.post.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var title: String,
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    var tag: PostTag,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    val writer: Member,

    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.REMOVE])
    val preferences: MutableList<Preference> = mutableListOf(),

    @ColumnDefault("0")
    @Column(name = "view_cnt", nullable = false)
    var viewCount: Int,

    @Column
    var isDeleted: Boolean = false,

) {
    companion object {
        fun fixture(
            id: UUID? = null,
            title: String = "postTest",
            content: String = "postContentTest",
            tagId: PostTag = PostTag.fixture(
                tagName = "testTag"
            ),
            writerId: Member = Member.fixture(),
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
            preferences: MutableList<Preference> = mutableListOf(),
            viewCount: Int = 0,
        ): Post {
            return Post(id, title, content, tagId, writerId, createdAt, updatedAt, preferences, viewCount)
        }
    }
}