package cc.connectcampus.connect_campus.global.config.security

import cc.connectcampus.connect_campus.domain.chat.exception.NotJoinedChatException
import cc.connectcampus.connect_campus.domain.chat.repository.ChatQuerydslRepository
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import lombok.extern.slf4j.Slf4j
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import java.util.*

@Slf4j
@Component
class StompHandler(
    private val tokenProvider: JwtTokenProvider,
    private val chatQuerydslRepository: ChatQuerydslRepository,
): ChannelInterceptor {

    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        if (accessor.command == StompCommand.CONNECT || accessor.command == StompCommand.SUBSCRIBE || accessor.command == StompCommand.SEND) {
            val token = accessor.getFirstNativeHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer") }
                ?.substring(7)
                ?: throw InvalidTokenException()

            if (!tokenProvider.validateToken(token)) throw InvalidTokenException()

            if (accessor.command != StompCommand.CONNECT) {
                logger.info("accessor.command: ${accessor.command}")
                val authentication = tokenProvider.getAuthentication(token)
                val userId = (authentication.principal as? CustomUser)?.id ?: throw InvalidTokenException()
                val chatId = UUID.fromString(accessor.destination?.split("/")?.last()) ?: throw EntityNotFoundException()
                verifyChatSubscription(userId, chatId)
            }
        }

        return super.preSend(message, channel)
    }

    private fun verifyChatSubscription(userId: UUID, chatId: UUID) {
        chatQuerydslRepository.notExists(chatId, userId)
            .takeIf { it }
            ?.let { throw NotJoinedChatException() }
    }
}