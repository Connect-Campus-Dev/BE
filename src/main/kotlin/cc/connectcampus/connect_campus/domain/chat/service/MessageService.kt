package cc.connectcampus.connect_campus.domain.chat.service

import cc.connectcampus.connect_campus.domain.chat.domain.Message
import cc.connectcampus.connect_campus.domain.chat.repository.ChatQuerydslRepository
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Slf4j
class MessageService(
    private val chatQuerydslRepository: ChatQuerydslRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val mongoTemplate: MongoTemplate,
    private val registry: SimpUserRegistry,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!
    //chat-queue 큐를 리스닝.
    //만약 이 큐에 메시지가 들어오면 여기서 처리한다.
    @RabbitListener(queues = ["chat-queue"])
    fun retrieveMessage(message: Message) {
        log.info("in the service")
        log.info(message.chatId.toString())
        log.info(message.senderId.toString())
        log.info(message.content)
        log.info(message.sentAt.toString())
        log.info(message.senderNickname)

        message.unReadMembers = chatQuerydslRepository.chatMembers(
            message.chatId
                ?: throw InvalidValueException(),
            message.senderId
                ?: throw InvalidValueException(),
        )

        messagingTemplate.convertAndSend("/topic/${message.chatId}", message)
        mongoTemplate.save(message)
    }

    @RabbitListener(queues = ["private-queue"])
    fun sendSubscriptionMessage(message: SubscriptionMessage) {
        val memberId = message.memberId.toString()
        messagingTemplate.convertAndSendToUser(memberId, "/queue/private", message)
    }
}
data class SubscriptionMessage(
    val action: String? = null,
    val memberId: UUID? = null,
    val targetId: UUID? = null,
)