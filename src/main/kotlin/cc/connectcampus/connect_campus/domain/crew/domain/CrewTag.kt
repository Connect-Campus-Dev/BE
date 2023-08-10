package cc.connectcampus.connect_campus.domain.crew.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class CrewTag(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    val TagName: String,
) {
    companion object {
        fun fixture(
            TagName: String = "testTag",
        ): CrewTag {
            return CrewTag(
                TagName = TagName,
            )
        }
    }
}