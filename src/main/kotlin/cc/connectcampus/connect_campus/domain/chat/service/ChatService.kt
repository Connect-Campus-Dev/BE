package cc.connectcampus.connect_campus.domain.chat.service

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.domain.Message
import cc.connectcampus.connect_campus.domain.chat.dto.ChatCreationRequest
import cc.connectcampus.connect_campus.domain.chat.dto.MessageRecordResponse
import java.util.*

interface ChatService {
    fun createChat(chatCreationRequest: ChatCreationRequest): Chat

    fun createChatTest(): Chat
    fun getUserChats(memberId: UUID): List<MessageRecordResponse?>
    fun getMessages(chatId: UUID): List<Message?>
}