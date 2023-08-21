package cc.connectcampus.connect_campus.domain.chat.controller

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.service.ChatService
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class ChatController(
    private val chatService: ChatService
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!

    //테스트용 컨트롤러
    @GetMapping("/chattest")
    fun createChatRoom(): Chat {
        return chatService.createChatTest()
    }


    // 유저가 처음 연결되면 호출되는 메서드
    @MessageMapping("/getChats")
    @SendToUser("/queue/chats")
    fun getChats(principal: Principal): List<ChatMember> {
        logger.info(principal.toString())
        return mutableListOf()
//        return chatService.getChatsForMember(principal.name) // principal.name을 사용하여 해당 유저의 채팅방 목록을 가져옴
    }
}