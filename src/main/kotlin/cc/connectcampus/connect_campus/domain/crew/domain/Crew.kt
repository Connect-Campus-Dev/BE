package cc.connectcampus.connect_campus.domain.crew.domain

import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class Crew(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID ?= null,

    @Column(nullable = false, unique = true)
    val name: String,

    val description: String? = null,

    @OneToOne
    val admin: Member,

    @CreationTimestamp
    val createdAt: LocalDateTime?= null,

    @OneToMany(mappedBy = "crew", cascade = [CascadeType.ALL])
    val members: MutableList<CrewMember> = mutableListOf(),

    @OneToMany
    val tags: List<CrewTag> = listOf(),
    ){
    companion object{
        fun fixture(
            name: String = "testCrew",
            description: String = "testDescription",
            admin: Member = Member.fixture(
                nickname = "adminUser",
            ),
            crewTag: CrewTag = CrewTag.fixture(
                TagName = "testTag",
            ),
        ): Crew {
            val crew = Crew(
                name = name,
                description = description,
                admin = admin,
                tags = listOf(crewTag),
            )

            val crewMember = CrewMember(
                crew = crew,
                member = admin,
            )
            crew.members.plus(crewMember)
            return crew
        }
    }
}