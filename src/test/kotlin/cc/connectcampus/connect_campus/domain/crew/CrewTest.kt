package cc.connectcampus.connect_campus.domain.crew

import cc.connectcampus.connect_campus.domain.crew.service.CrewServiceV0
import cc.connectcampus.connect_campus.domain.crew.domain.Story
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewEnrollRequest
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.crew.repository.StoryRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class CrewTest(
    @Autowired val storyRepository: StoryRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val crewRepository: CrewRepository,
    @Autowired val crewService: CrewServiceV0,
) {

    lateinit var testMember: Member
    @BeforeEach
    fun before(){
        testMember = Member.fixture(
            nickname="TestMember",
        )
    }

    @Test
    @Transactional
    fun `Story Repository Basic Test`(){
        memberRepository.save(testMember)
        val testStory = Story(
            title="TestTitle",
            content="TestContent",
            author=testMember)
        storyRepository.save(testStory)
        assertThat(storyRepository.findAll().count()).isEqualTo(1)
    }

    @Test
    @Transactional
    fun `Crew added correctly`(){
        memberRepository.save(testMember)
        val crewEnrollRequest = CrewEnrollRequest(
            name="TestCrew",
            description="TestDescription",
            admin=testMember,
            tags = listOf("TestTag1", "TestTag2"),
        )
        crewService.enroll(crewEnrollRequest)
        assertThat(crewRepository.findAll().count()).isEqualTo(1)
        assertThat(crewRepository.findByName("TestCrew")?.name).isEqualTo("TestCrew")
        assertThat(testMember.joinedCrew[0].crew.name).isEqualTo("TestCrew")
    }
}