package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.CrewTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CrewTagRepository: JpaRepository<CrewTag,Long>{
}