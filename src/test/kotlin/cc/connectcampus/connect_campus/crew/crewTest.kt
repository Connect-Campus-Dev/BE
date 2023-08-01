package cc.connectcampus.connect_campus.crew

import cc.connectcampus.connect_campus.domain.crew.domain.Story
import cc.connectcampus.connect_campus.domain.crew.repository.storyRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.memberRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest
class crewTest(
    @Autowired val storyRepository: storyRepository,
    @Autowired val memberRepository: memberRepository,
) {

    @Test
    fun `Story Repository Basic Test`(){
        val testMember = Member(
            name="TestMember"
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