package cc.connectcampus.connect_campus.domain.chat.controller

import cc.connectcampus.connect_campus.domain.chat.dto.ChatDto
import cc.connectcampus.connect_campus.domain.chat.service.ChatMessage
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

//@Controller
//class MessageController(
//    private val template: RabbitTemplate,
//
//) {
//    companion object {
//        const val CHAT_EXCHANGE_NAME = "chat.exchange"
//        const val CHAT_QUEUE_NAME = "chat.queue"
//    }
//
//    @MessageMapping("chat.enter.{chatRoomId}")
//    fun enter(chatDto: ChatDto, @DestinationVariable chatRoomId: String) {
//        chatDto.message = "${chatDto.memberId}님이 입장하셨습니다."
//        chatDto.regDate = LocalDateTime.now()
//        template.convertAndSend(CHAT_EXCHANGE_NAME, "room.$chatRoomId", chatDto)
//    }
//
//    @MessageMapping("chat.message.{chatRoomId}")
//    fun send(chatDto: ChatDto, @DestinationVariable chatRoomId: String) {
//        chatDto.regDate = LocalDateTime.now()
//
//        template.convertAndSend(CHAT_EXCHANGE_NAME, "room.$chatRoomId", chatDto)
//    }
//
//    @RabbitListener(queues = [CHAT_QUEUE_NAME])
//    fun receive(chatDto: ChatDto) {
//        println(chatDto.message)
//    }
//}

//@RestController
//@Slf4j
//class MessageController(
//    private val rabbitTemplate: RabbitTemplate,
//) {
//    private val log = LoggerFactory.getLogger(this.javaClass)!!
//    @PostMapping("/send")
//    fun sendMessage(@RequestBody message: ChatMessage) {
//
//        log.info(message.message)
//        log.info(message.chatRoomId)
//        log.info(message.userId)
//
//        rabbitTemplate.convertAndSend("chat-queue", message)
//    }
//}

@RestController
class MessageController(
    private val rabbitTemplate: RabbitTemplate
) {
    @MessageMapping("/chat/{chatId}")
    fun sendMessage(
//        @DestinationVariable chatId: String,
        @RequestBody message: ChatMessage,
    ) {
        rabbitTemplate.convertAndSend("chat-queue", message);
    }
}