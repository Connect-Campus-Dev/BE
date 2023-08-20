package cc.connectcampus.connect_campus.domain.chat.repository

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository: JpaRepository<Chat, Long> {

}