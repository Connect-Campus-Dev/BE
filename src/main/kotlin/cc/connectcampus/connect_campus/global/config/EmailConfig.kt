package cc.connectcampus.connect_campus.global.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "email.verification")
class EmailConfig {
    lateinit var subject: String
    lateinit var text: String
}