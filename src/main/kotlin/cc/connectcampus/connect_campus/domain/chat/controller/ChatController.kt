package cc.connectcampus.connect_campus.domain.chat.controller

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.domain.Message
import cc.connectcampus.connect_campus.domain.chat.dto.MessageRecordResponse
import cc.connectcampus.connect_campus.domain.chat.service.ChatService
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.global.config.security.InvalidTokenException
import lombok.extern.slf4j.Slf4j
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.UUID

@RestController
@Slf4j
class ChatController(
    private val chatService: ChatService
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!

    @PostMapping("/chattest")
    fun createChatRoom(): Chat {
        return chatService.createChatTest()
    }

    @GetMapping("/chat/{chatId}")
    fun getChat(@PathVariable chatId: UUID): List<Message?> {
        logger.info("Hello~")
        return chatService.getMessages(chatId)
    }


    // 유저가 처음 연결되면 호출되는 메서드
    // @SendToUser 어노테이션을 사용할 때 Spring은 현재 인증된 사용자의 세션을 자동으로 식별한다.
    // 해당 세션에 연결된 클라이언트에게만 메시지를 전송한다.

    //todo: StickySession 적용시 문제는 없지만, 만약 StickySession 적용을 안한다면, MQ에 메시지를 넣고, 큐 리스터를 만들어서 처리해야함.
    //todo: --> 따라서 StickySession은 일단 적용하자.
    @MessageMapping("/getChats")
    @SendToUser("/queue/private")
    fun getChats(authentication: Authentication): List<MessageRecordResponse?> {

        logger.info("===========ChatController===========")
        val memberId: UUID = (authentication.principal as CustomUser).id ?: throw InvalidTokenException()
        logger.info("memberId: $memberId")
        logger.info("memberNickname: ${(authentication.principal as CustomUser).nickname}")

        //각 채팅방 별, 마지막 메시지(내용, 보낸시각)와 해당 유저가 읽지 않은 메시지 수
        //MessageRecordResponse 에서 정의

        return chatService.getUserChats(memberId)
    }
}