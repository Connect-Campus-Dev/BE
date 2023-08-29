package cc.connectcampus.connect_campus.domain.chat.dto

data class MessageRequest(
    val chatId: String = "",
    val content: String = "",
)