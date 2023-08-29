package cc.connectcampus.connect_campus.domain.chat.service

import cc.connectcampus.connect_campus.domain.chat.domain.*
import cc.connectcampus.connect_campus.domain.chat.dto.*
import cc.connectcampus.connect_campus.domain.chat.exception.ChatNotFoundException
import cc.connectcampus.connect_campus.domain.chat.repository.ChatMemberRepository
import cc.connectcampus.connect_campus.domain.chat.repository.ChatQuerydslRepository
import cc.connectcampus.connect_campus.domain.chat.repository.ChatRepository
import cc.connectcampus.connect_campus.domain.chat.repository.MessageMongoRepository
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Service
@Transactional
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val memberRepository: MemberRepository,
    private val messageMongoRepository: MessageMongoRepository,
    private val chatQuerydslRepository: ChatQuerydslRepository,
): ChatService {

    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!

    override fun createChat(chatCreationRequest: ChatCreationRequest): Chat {
        val chat: Chat = chatCreationRequest.toChat()

        return chatRepository.save(chat)
    }

    override fun createChatTest(): Chat {
        val memberId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id

        val member = memberRepository.findById(memberId!!)

        val chat = Chat(
            title = "테스트",
            type = ChatType.CREW,
            members = mutableListOf()
        )
        val chatMember = ChatMember(chat, member!!)
        chat.members.add(chatMember)

        chatRepository.save(chat)

        val welcomeMessage = Message(
            senderId = null,
            senderNickname = null,
            chatId = chat.id,
            content = "채팅방이 개설되었어요.",
            type = MessageType.SYSTEM,
        )

        chatMemberRepository.save(chatMember)
        messageMongoRepository.save(welcomeMessage)

        return chat
    }

    override fun getUserChats(memberId: UUID): List<MessageRecordResponse> {
        // 유저가 속한 채팅방 목록을 가져오기
        val chats: List<Chat> = chatMemberRepository.findAllByMemberId(memberId).map { it
            ?.chat
            ?: return mutableListOf()
        }

        // 각 채팅방에 대한 정보를 가공
        return chats.map { chat ->
            val members = chat.members

            //members 에서 id가 memberId인 member 찾기
            val findMember = members.find { it.member.id == memberId } ?: throw MemberNotFoundException()
            var joinedAt: LocalDateTime = findMember.joinedAt ?: throw MemberNotFoundException()
            joinedAt = joinedAt.minusHours(9).minusSeconds(5)

            logger.info("findMember: ${findMember.id}")
            logger.info("joinedAt: $joinedAt")
            logger.info("chatId: ${chat.id}")

            //해당 유저가 가입한 이후의 기록에서, 마지막 메시지 가져오기
            val lastMessage = messageMongoRepository
                        .findFirstByChatIdAndSentAtGreaterThanOrderBySentAtDesc(
                            chat.id!!, joinedAt
                        )

            logger.info("lastMessage: $lastMessage")

            //해당 유저가 가입한 이후의 기록에서, 안읽은 메시지 개수 가져오기
            val unreadMessageCount = messageMongoRepository
                .countByChatIdAndSentAtGreaterThanAndUnReadMembersNotContains(
                    chat.id, joinedAt, memberId
                )

            logger.info("unreadMessageCount: $unreadMessageCount")

            MessageRecordResponse(
                chat = ChatInfo(
                    id = chat.id,
                    title = chat.title,
                    type = chat.type,
                    memberCount = members.size,
                    unreadMessageCount = unreadMessageCount
                ),
                lastMessage = MessageInfo(
                    senderId = lastMessage!!.senderId,
                    senderNickname = lastMessage.senderNickname,
                    content = lastMessage.content!!,
                    sentAt = lastMessage.sentAt!!
                )
            )
        }
    }


    override fun getMessages(chatId: UUID): List<Message?> {
        return mutableListOf()
    }


    //    override fun getMessages(chatId: String) {
//        return chatRepository.findById(chatId)
//    }
}