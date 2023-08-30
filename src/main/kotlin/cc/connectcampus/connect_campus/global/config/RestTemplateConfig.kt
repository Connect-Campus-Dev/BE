package cc.connectcampus.connect_campus.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets

@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder
                .requestFactory {
                    ->      BufferingClientHttpRequestFactory(
                            SimpleClientHttpRequestFactory()
                    )
                }
                .additionalMessageConverters(
                        StringHttpMessageConverter(
                                StandardCharsets.UTF_8
                        )
                )
                .build()
    }

}
