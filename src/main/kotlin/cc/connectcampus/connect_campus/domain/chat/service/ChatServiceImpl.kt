package cc.connectcampus.connect_campus.domain.chat.service

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.domain.ChatMember
import cc.connectcampus.connect_campus.domain.chat.domain.ChatType
import cc.connectcampus.connect_campus.domain.chat.dto.ChatCreationRequest
import cc.connectcampus.connect_campus.domain.chat.repository.ChatMemberRepository
import cc.connectcampus.connect_campus.domain.chat.repository.ChatRepository
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val memberRepository: MemberRepository,
): ChatService {

    override fun createChat(chatCreationRequest: ChatCreationRequest): Chat {
        val chat: Chat = chatCreationRequest.toChat()

        return chatRepository.save(chat)
    }

    override fun createChatTest(): Chat {
        val memberId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).id

        val member = memberRepository.findById(memberId!!)

        val chat = Chat("테스트", ChatType.CREW, mutableListOf())
        val chatMember = ChatMember(chat, member!!)
        chatRepository.save(chat)
        chatMemberRepository.save(chatMember)

        return chat
    }


//    override fun getMessages(chatId: String) {
//        return chatRepository.findById(chatId)
//    }
}