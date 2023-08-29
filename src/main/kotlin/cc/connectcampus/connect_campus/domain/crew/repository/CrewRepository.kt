package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CrewRepository: JpaRepository<Crew, Long> {
    fun existsByName(name: String): Boolean
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN false ELSE true END FROM Crew c WHERE c.id = :id")
    fun notExistsById(id: UUID): Boolean
    fun findById(id: UUID): Crew
}