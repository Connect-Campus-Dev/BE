package cc.connectcampus.connect_campus.domain.chat.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.LocalDateTime

//data class ChatDto(
//    var id: Long,
//    var chatRoom: Long?=null,
//    var memberId: Long?=null,
//    var message: String?=null,
//    var region: String?=null,
//
//    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
//    var regDate: LocalDateTime,
//) {
//}

data class ChatDto(
    val from: String,
    val text: String
)