package cc.connectcampus.connect_campus.domain.crew

import cc.connectcampus.connect_campus.domain.crew.domain.Story
import cc.connectcampus.connect_campus.domain.crew.repository.StoryRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class CrewTest(
    @Autowired val storyRepository: StoryRepository,
    @Autowired val memberRepository: MemberRepository,
) {

    @Test
    @Transactional
    fun `Story Repository Basic Test`(){
        val testMember = Member.fixture(
            nickname="TestMember"
        )
        memberRepository.save(testMember)
        val testStory = Story(
            title="TestTitle",
            content="TestContent",
            author=testMember)
        storyRepository.save(testStory)
        assertThat(storyRepository.findAll().count()).isEqualTo(1)
    }
}