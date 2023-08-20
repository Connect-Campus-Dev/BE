package cc.connectcampus.connect_campus.domain.chat.dto

import java.time.LocalDateTime

/**
 * [채팅방 입장시 1회]
 * 채팅방 제목
 *
 *
 *
 * [계속 필요한 데이터]
 * 보낸사람
 * 보낸사람 프로필
 * 보낸시각
 * 메시지 내용
 *
 */
data class MessageResponse(
    val chatId: String,
    val userId: String,
    val userName: String,
    val message: String,
    val createdAt: LocalDateTime,

)