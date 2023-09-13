package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.LoveProfileImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LoveProfileImageRepository: JpaRepository<LoveProfileImage, UUID> {
    fun findByLoveProfileId(loveProfileId: UUID): List<LoveProfileImage>
}