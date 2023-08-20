package cc.connectcampus.connect_campus.domain.chat.service

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
@Slf4j
class MessageService(
    private val messagingTemplate: SimpMessagingTemplate,
    private val mongoTemplate: MongoTemplate,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    //chat-queue 큐를 리스닝.
    //만약 이 큐에 메시지가 들어오면 여기서 처리한다.
    @RabbitListener(queues = ["chat-queue"])
    fun retrieveMessage(message: ChatMessage) {
        log.info("in the service")
        log.info(message.toString())
        messagingTemplate.convertAndSend("/topic/${message.chatId}", message)
        mongoTemplate.save(message)
    }
}

data class ChatMessage(
    val chatId: String = "",
    val userId: String = "",
    val message: String = "",
)