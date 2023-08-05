package cc.connectcampus.connect_campus.domain.univ.repository

import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UnivRepository: JpaRepository<Univ, Long> {
}