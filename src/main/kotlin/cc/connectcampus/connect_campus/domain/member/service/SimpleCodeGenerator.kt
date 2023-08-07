package cc.connectcampus.connect_campus.domain.member.service

import org.springframework.stereotype.Service
import java.util.*

@Service
class SimpleCodeGenerator: CodeGenerator {
    private val random = Random()
    override fun generate(): String {
        val number = random.nextInt(10000)
        return String.format("%04d", number)
    }
}