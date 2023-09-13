package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.LoveProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LoveProfileRepository :JpaRepository<LoveProfile, UUID> {

    fun findByMemberId(memberId: UUID): LoveProfile?

}