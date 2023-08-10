package cc.connectcampus.connect_campus.domain.crew

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.domain.CrewTag
import cc.connectcampus.connect_campus.domain.crew.service.CrewServiceV0
import cc.connectcampus.connect_campus.domain.crew.domain.Story
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import cc.connectcampus.connect_campus.domain.crew.exception.CrewMemberJoinedException
import cc.connectcampus.connect_campus.domain.crew.exception.CrewNameDuplicateException
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewTagRepository
import cc.connectcampus.connect_campus.domain.crew.repository.StoryRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import cc.connectcampus.connect_campus.domain.model.Email
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class CrewTest(
    @Autowired val storyRepository: StoryRepository,
    @Autowired val memberRepository: MemberRepository,
    @Autowired val crewRepository: CrewRepository,
    @Autowired val crewService: CrewServiceV0,
    @Autowired val crewTagRepository: CrewTagRepository,
) {

    lateinit var testMember1: Member
    lateinit var testMember2: Member
    lateinit var testTag1: CrewTag
    lateinit var testTag2: CrewTag
    lateinit var crew1: Crew

    @BeforeEach
    fun before(){
        testMember1 = Member.fixture(
            nickname="TestMember1",
        )
        testMember2 = Member.fixture(
            nickname="TestMember2",
            email= Email("ho7221@korea.ac.kr"),
        )
        testTag1 = CrewTag.fixture(
            "TestTag",
        )
        testTag2 = CrewTag.fixture(
            "TestTag2",
        )
        crew1 = Crew(
            name="TestCrew",
            description="TestDescription",
            admin=testMember1,
            tags = listOf(testTag1, testTag2),
        )
        memberRepository.save(testMember1)
        memberRepository.save(testMember2)
        crewTagRepository.save(testTag1)
        crewTagRepository.save(testTag2)
    }

    @Test
    @Transactional
    fun `Crew added correctly`(){
        val crewCreationRequest = CrewCreationRequest(
            name="AddedCrew",
            description="TestDescription",
            adminId = testMember1.id!!,
            tags = listOf("TestTag1", "TestTag2"),
        )
        val createdCrew = crewService.create(crewCreationRequest)
        assertThat(crewRepository.existsByName("AddedCrew")).isTrue()
        assertThat(testMember1.joinedCrew[0].crew.name).isEqualTo("AddedCrew")
        assertThat(createdCrew.members.count()).isEqualTo(1)
        assertThat(createdCrew.members[0].member.nickname).isEqualTo("TestMember1")
    }

    @Test
    @Transactional
    fun `Crew Duplicate Check`(){
        crewRepository.save(crew1)
        val crew2CreationRequest = CrewCreationRequest(
            name="TestCrew",
            description="TestDescription",
            adminId = testMember2.id!!,
            tags = listOf("TestTag2"),
        )
        assertThrows<CrewNameDuplicateException>{
            crewService.create(crew2CreationRequest)
        }
    }

    @Test
    @Transactional
    fun `Already Joined Crew check`(){
        val crewCreationRequest = CrewCreationRequest(
            name="AddedCrew",
            description="TestDescription",
            adminId = testMember1.id!!,
            tags = listOf("TestTag1", "TestTag2"),
        )
        val createdCrew = crewService.create(crewCreationRequest)

        assertThrows<CrewMemberJoinedException>{
            crewService.join(createdCrew.id!!, testMember1.id!!)
        }
    }

    @Test
    @Transactional
    fun `Story Repository Basic Test`(){
        val testStory = Story(
            title="TestTitle",
            content="TestContent",
            author=testMember1)
        storyRepository.save(testStory)
        assertThat(storyRepository.findAll().count()).isEqualTo(1)
    }
}