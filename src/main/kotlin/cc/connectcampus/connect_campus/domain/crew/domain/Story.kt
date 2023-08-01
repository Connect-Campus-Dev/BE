package cc.connectcampus.connect_campus.domain.crew.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Story(
    @Id
    @GeneratedValue
    var id: Long? = null,
    var title: String,
    var content: String,
    @ManyToOne var author: Member,
    var addedAt: LocalDateTime = LocalDateTime.now(),
) {
}