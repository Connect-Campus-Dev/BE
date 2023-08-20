package cc.connectcampus.connect_campus.domain.chat.controller

import cc.connectcampus.connect_campus.domain.chat.domain.Chat
import cc.connectcampus.connect_campus.domain.chat.dto.ChatCreationRequest
import cc.connectcampus.connect_campus.domain.chat.service.ChatService
import cc.connectcampus.connect_campus.domain.chat.service.ChatServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(
    private val chatService: ChatService
) {

    //테스트용 컨트롤러
    @GetMapping("/chattest")
    fun createChatRoom(): Chat {
        return chatService.createChatTest()
    }
}