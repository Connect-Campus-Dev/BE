package cc.connectcampus.connect_campus.domain.crew

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.domain.CrewTag
import cc.connectcampus.connect_campus.domain.crew.service.CrewServiceV0
import cc.connectcampus.connect_campus.domain.crew.domain.Story
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import cc.connectcampus.connect_campus.domain.crew.exception.CrewJoinFoundException
import cc.connectcampus.connect_campus.domain.crew.exception.CrewMemberJoinedException
import cc.connectcampus.connect_campus.domain.crew.exception.CrewNameDuplicateException
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewTagRepository
import cc.connectcampus.connect_campus.domain.crew.repository.StoryRepository
import cc.connectcampus.connect_campus.domain.crew.service.CrewAdminServiceV0
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
    @Autowired val crewAdminService: CrewAdminServiceV0,
    @Autowired val crewTagRepository: CrewTagRepository,
) {

    lateinit var testMember1: Member
    lateinit var testMember2: Member
    lateinit var testMember3: Member
    lateinit var testTag1: CrewTag
    lateinit var testTag2: CrewTag
    lateinit var testCrew1: Crew

    @BeforeEach
    fun before(){
        testMember1 = Member.fixture(
            nickname="TestMember1",
        )
        testMember2 = Member.fixture(
            nickname="TestMember2",
            email= Email("ho7221@korea.ac.kr"),
        )
        testMember3 = Member.fixture(
            nickname="TestMember3",
            email= Email("test3@korea.ac.kr"),
        )
        testTag1 = CrewTag.fixture(
            "TestTag",
        )
        testTag2 = CrewTag.fixture(
            "TestTag2",
        )
        testCrew1 = Crew(
            name="TestCrew",
            description="TestDescription",
            admin=testMember1,
            tags = listOf(testTag1, testTag2),
        )
        memberRepository.save(testMember1)
        memberRepository.save(testMember2)
        memberRepository.save(testMember3)
        crewTagRepository.save(testTag1)
        crewTagRepository.save(testTag2)
    }

    @Test
    @Transactional
    fun `Crew added correctly`(){
        val crewCreationRequest = CrewCreationRequest(
            name="AddedCrew",
            description="TestDescription",
            tags = listOf("TestTag1", "TestTag2"),
        )
        val createdCrew = crewService.create(crewCreationRequest,testMember1.id!!)
        crewService.join(createdCrew.id!!,testMember1.id!!)
        assertThat(crewRepository.existsByName("AddedCrew")).isTrue()
        assertThat(testMember1.joinedCrew[0].crew.name).isEqualTo("AddedCrew")
        assertThat(createdCrew.members.count()).isEqualTo(1)
        assertThat(createdCrew.members[0].member.nickname).isEqualTo("TestMember1")
    }

    @Test
    @Transactional
    fun `Crew JoinRequest sent & read correctly`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)
        // testMember1 is admin, testMember2 is requesting user
        crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        val joinRequestList = crewAdminService.loadJoinRequest(testCrew1.id!!,testMember1.id!!)
        assertThat(joinRequestList.size).isEqualTo(1)
        assertThat(joinRequestList[0].member.id).isEqualTo(testMember2.id)
    }

    @Test
    @Transactional
    fun `Crew JoinRequest admitted and joined correctly`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)
        // testMember1 is admin, testMember2 is requesting user
        crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        crewAdminService.permitJoinRequest(
            crewAdminService.loadJoinRequest(testCrew1.id!!,testMember1.id!!)[0].id!!,
            testMember1.id!!
        )
    }

    @Test
    @Transactional
    fun `Crew Duplicate Check`(){
        crewRepository.save(testCrew1)
        val crew2CreationRequest = CrewCreationRequest(
            name="TestCrew",
            description="TestDescription",
            tags = listOf("TestTag2"),
        )
        assertThrows<CrewNameDuplicateException>{
            crewService.create(crew2CreationRequest,testMember2.id!!)
        }
    }

    @Test
    @Transactional
    fun `Already Joined Crew check`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)
        crewService.join(testCrew1.id!!,testMember2.id!!)

        assertThrows<CrewMemberJoinedException>{
            crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        }
    }

    @Test
    @Transactional
    fun `JoinRequest listed in order`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)

        crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        crewService.joinRequest(testCrew1.id!!,testMember3.id!!)

        val requestList = crewAdminService.loadJoinRequest(testCrew1.id!!,testMember1.id!!)
        assertThat(requestList[0].requestTime < requestList[1].requestTime)
    }

    @Test
    @Transactional
    fun `Already Requested CrewJoinRequest check`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)
        crewService.joinRequest(testCrew1.id!!,testMember2.id!!)

        assertThrows<CrewJoinFoundException>{
            crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        }
    }

    @Test
    @Transactional
    fun `JoinRequest denied and deleted check`(){
        crewRepository.save(testCrew1)
        crewService.join(testCrew1.id!!,testMember1.id!!)

        crewService.joinRequest(testCrew1.id!!,testMember2.id!!)
        val requestList = crewAdminService.loadJoinRequest(testCrew1.id!!,testMember1.id!!)
        crewAdminService.denyJoinRequest(requestList[0].id!!,testMember1.id!!)
        assertThat(crewAdminService.loadJoinRequest(testCrew1.id!!,testMember1.id!!).size).isEqualTo(0)
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