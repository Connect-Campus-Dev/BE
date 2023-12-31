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
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.context.SecurityContextHolder
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
        logger.info(message.toString())
        logger.info(channel.toString())
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: throw InvalidTokenException()

        if (accessor.command == StompCommand.CONNECT || accessor.command == StompCommand.SUBSCRIBE || accessor.command == StompCommand.SEND) {
            logger.info("===============================")
            logger.info("command: ${accessor.command.toString()}")
            logger.info("destination: ${accessor.destination.toString()}")

            val token = accessor.getFirstNativeHeader("Authorization")
                ?.takeIf { it.startsWith("Bearer") }
                ?.substring(7)
                ?: throw InvalidTokenException()

            logger.info("token: $token")

            if (!tokenProvider.validateToken(token)) throw InvalidTokenException()
            val authentication = tokenProvider.getAuthentication(token)

            logger.info("authentication: $authentication")
            SecurityContextHolder.getContext().authentication = authentication
            accessor.user = authentication

            //채팅방을 구독하는 경우, 해당 채팅방에 가입되어있는지 확인한다.
            //채팅방에 메시지를 보내는 경우, 해당 채팅방에 가입되어있는지 확인한다.
            if ((accessor.command == StompCommand.SUBSCRIBE && accessor.destination?.startsWith("/user/queue/private") == false) ||
                (accessor.command == StompCommand.SEND && accessor.destination?.startsWith("/app/getChats") == false)) {
                logger.info("Hello! ${accessor.destination}")
                val userId = (authentication.principal as? CustomUser)?.id ?: throw InvalidTokenException()
                val chatId = UUID.fromString(accessor.destination?.split("/")?.last()) ?: throw EntityNotFoundException()
                verifyChatSubscription(userId, chatId)
            }
            if(accessor.command == StompCommand.SEND) {
                //메시지 검증 필요
                //content가 blank인지 확인
                logger.info("Send!")
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