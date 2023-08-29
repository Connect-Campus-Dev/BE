package cc.connectcampus.connect_campus.domain.chat.dto

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.domain.ChatType
import cc.connectcampus.connect_campus.domain.member.domain.Member
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.UUID

data class ChatCreationRequest(
    @field:NotEmpty @field:Size(min = 1, max = 20)
    val title: String,

    @field:NotEmpty
    val type: ChatType,

    @field:Size(min = 1)
    val members: MutableList<ChatMember>,
) {
    fun toChat(): Chat {
        return Chat(
            title = title,
            type = type,
            members = members,
        )
    }
}