package cc.connectcampus.connect_campus.global.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableRabbit
class RabbitMQConfig {

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }
    @Bean
    fun chatQueue(): Queue {
        return Queue("chat-queue")
    }

    @Bean
    fun chatFanoutExchange(): FanoutExchange {
        return FanoutExchange("chat-fanout")
    }

    @Bean
    fun privateFanoutExchange(): FanoutExchange {
        return FanoutExchange("private-fanout")
    }

    @Bean
    fun privateQueue(): Queue {
        return Queue("private-queue")
    }

    @Bean
    fun binding(privateFanoutExchange: FanoutExchange, privateQueue: Queue): Binding {
        return BindingBuilder.bind(privateQueue).to(privateFanoutExchange)
    }
}