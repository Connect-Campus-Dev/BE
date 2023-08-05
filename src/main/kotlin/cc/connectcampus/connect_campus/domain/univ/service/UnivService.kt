package cc.connectcampus.connect_campus.domain.univ.service

import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class SchoolService(
    private val univRepository: UnivRepository,
) {
    private val univCache: MutableMap<String, Univ> = mutableMapOf()


    //애플리케이션 로드 시점에 학교 목록을 캐시메모리에 저장
    //대부분의 기능에서 학교 이름이 자주 조회된다. DB 접근을 줄이기 위해 캐시메모리에 저장
    //Redis로도 전환 고려
    @PostConstruct
    fun initializeSchoolCache() {
        univCache.putAll(univRepository.findAll().associateBy { it.emailDomain })
    }

    fun getSchoolNameByEmailDomain(emailDomain: String): String {
        return univCache[emailDomain]?.name
            ?: throw IllegalArgumentException("School not found for email domain")
    }
}
