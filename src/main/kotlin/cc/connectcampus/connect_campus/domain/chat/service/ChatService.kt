package cc.connectcampus.connect_campus.domain.chat.service

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.dto.ChatCreationRequest

interface ChatService {
    fun createChat(chatCreationRequest: ChatCreationRequest): Chat

    fun createChatTest(): Chat
}