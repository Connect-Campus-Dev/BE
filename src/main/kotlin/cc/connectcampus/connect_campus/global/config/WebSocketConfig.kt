package cc.connectcampus.connect_campus.global.config

import cc.connectcampus.connect_campus.global.config.security.StompHandler
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.util.AntPathMatcher
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
class WebSocketConfig(
    private val stompHandler: StompHandler,
) : WebSocketMessageBrokerConfigurer {

    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)!!

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // /app, /user으로 시작되는 메시지는 컨트롤러의 @MessageMapping으로 라우팅 된다
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/user")

        //topic으로 시작하는 메시지는 메시지 브로커로 라우팅 된다
        config.enableStompBrokerRelay("/topic", "/queue")
            .setRelayHost("localhost")
            .setRelayPort(61613)
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // /chat으로 stomp 연결을 맺는다
        registry
            //todo: setAllowedOrigins("*")는 보안상 좋지 않다. 추후 수정 필요
            //todo: WebSocketErrorHandler() 에러 핸들러 추가 필요
//            .setErrorHandler(WebSocketErrorHandler())
            .addEndpoint("/chat")
            .setAllowedOrigins("*")
//            .addInterceptors(stompHandshakeHandler)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompHandler) // 인터셉터 추가
    }
}