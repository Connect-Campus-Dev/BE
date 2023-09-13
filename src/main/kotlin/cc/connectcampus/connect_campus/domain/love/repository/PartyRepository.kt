package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PartyRepository: JpaRepository<Party, UUID> {

}