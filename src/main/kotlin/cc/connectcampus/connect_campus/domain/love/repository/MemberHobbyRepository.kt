package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.MemberHobby
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberHobbyRepository: JpaRepository<MemberHobby, UUID> {
    fun findByLoveProfileId(loveProfileId: UUID): List<MemberHobby>
}