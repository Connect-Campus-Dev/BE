package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.MemberPersonality
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MemberPersonalityRepository: JpaRepository<MemberPersonality, UUID> {
    fun findByLoveProfileId(loveProfileId: UUID): List<MemberPersonality>
}