package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CrewRepository: JpaRepository<Crew, Long> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Crew?
}