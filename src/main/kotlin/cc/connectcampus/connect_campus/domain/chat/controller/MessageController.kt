package cc.connectcampus.connect_campus.domain.chat.controller

import cc.connectcampus.connect_campus.domain.chat.domain.Message
import cc.connectcampus.connect_campus.domain.chat.domain.MessageType
import cc.connectcampus.connect_campus.domain.chat.dto.MessageRequest
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MessageController(
    private val rabbitTemplate: RabbitTemplate
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!
    @MessageMapping("/chat/{chatId}")
    fun sendMessage(
        @RequestBody messageRequest: MessageRequest,
        authentication: Authentication,
    ) {
        logger.info("===========MessageController===========")
        logger.info("messageRequest: $messageRequest")
        logger.info("authentication: $authentication")
        logger.info("authentication.principal: ${(authentication.principal as CustomUser).id}")
        logger.info("===========MessageControllerEnd===========")
        val message = Message(
            chatId = UUID.fromString(messageRequest.chatId),
            senderId = (authentication.principal as CustomUser).id!!,
            senderNickname = (authentication.principal as CustomUser).nickname!!,
            content = messageRequest.content,
            type = MessageType.USER,
        )
        rabbitTemplate.convertAndSend("chat-queue", message);
    }
}