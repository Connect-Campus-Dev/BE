package cc.connectcampus.connect_campus.domain.univ.service

import cc.connectcampus.connect_campus.domain.model.Email
import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class UnivService(
        private val univRepository: UnivRepository,
) {
    fun getEmailDomainBySchoolName(name: String) : String {
        val savedUniv = univRepository.findByName(name) ?: throw EntityNotFoundException()
        return savedUniv.emailDomain
    }
    fun getSchoolNameByEmailDomain(emailDomain: Email): String {
        val savedUniv = univRepository.findByEmailDomain(emailDomain.getDomain()) ?: throw EntityNotFoundException()
        return savedUniv.name
    }
}
