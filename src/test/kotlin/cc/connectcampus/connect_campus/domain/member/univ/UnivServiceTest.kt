package cc.connectcampus.connect_campus.domain.member.univ

import cc.connectcampus.connect_campus.domain.univ.domain.Univ
import cc.connectcampus.connect_campus.domain.univ.repository.UnivRepository
import cc.connectcampus.connect_campus.domain.univ.service.UnivService
import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

@SpringBootTest
class UnivServiceTest (
    @Autowired val univService: UnivService,
    @Autowired val univRepository: UnivRepository,
){
    @BeforeEach
    fun before(){
        val testUniv: Univ = Univ(
                name = "인하대학교",
                emailDomain = "inha.edu.kr"
        )
        univRepository.save(testUniv)
    }
    @Test
    @Transactional
    fun `이메일 도메인 by 학교 이름`(){
        // 1. 예상 데이터
        val getEmailDomain = univService.getEmailDomainBySchoolName("인하대학교")
        // 2. 비교 및 검증
        assertThat("inha.edu.kr").isEqualTo(getEmailDomain)
    }
    @Test
    @Transactional
    fun `학교 이름 by 이메일 도메인`(){
        // 1. 예상 데이터
        val getName = univService.getSchoolNameByEmailDomain("inha.edu.kr")
        // 2. 비교 및 검증
        assertThat("인하대학교").isEqualTo(getName)
    }
    @Test
    @Transactional
    fun `학교 이름 예외 처리`() {
        assertThrows<EntityNotFoundException> {
            univService.getEmailDomainBySchoolName("대학교")
        }
    }
    @Test
    @Transactional
    fun `이메일 도메인 예외 처리`(){
        assertThrows<EntityNotFoundException> {
            univService.getSchoolNameByEmailDomain("edu.kr")
        }
    }
}