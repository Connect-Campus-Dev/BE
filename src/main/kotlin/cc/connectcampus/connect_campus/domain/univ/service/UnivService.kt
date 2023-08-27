package cc.connectcampus.connect_campus.domain.univ.service

import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class UnivService(
        private val univRepository: UnivRepository,
) {
    fun getEmailDomainBySchoolName(name: String) : String {
        val savedUniv = univRepository.findByName(name) ?: throw EntityNotFoundException()
        return savedUniv.emailDomain
    }

    fun getSchoolNameByEmailDomain(emailDomain: String): String {
        val savedUniv = univRepository.findByEmailDomain(emailDomain) ?: throw EntityNotFoundException()
        return savedUniv.name
    }
}
