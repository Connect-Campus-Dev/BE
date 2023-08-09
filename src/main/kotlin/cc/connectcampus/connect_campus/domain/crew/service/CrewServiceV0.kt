package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import cc.connectcampus.connect_campus.domain.crew.domain.CrewTag
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewEnrollRequest
import cc.connectcampus.connect_campus.domain.crew.exception.*
import cc.connectcampus.connect_campus.domain.crew.repository.CrewMemberRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewTagRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CrewServiceV0(
    val crewRepository: CrewRepository,
    val crewTagRepository: CrewTagRepository,
    val crewMemberRepository: CrewMemberRepository,
): CrewService{
    @Transactional
    override fun enroll(crewEnrollRequest: CrewEnrollRequest): Crew {
        // Crew Name Length & Duplicate Check
        if(crewEnrollRequest.name.length<2 || crewEnrollRequest.name.length>10) throw CrewNameLengthException()
        if(crewRepository.existsByName(crewEnrollRequest.name)) throw CrewNameDuplicateException()
        
        // Crew Description Length Check
        if(crewEnrollRequest.description.length>100) throw CrewDescriptionException()

        val enrollCrew = Crew(
            name = crewEnrollRequest.name,
            description = crewEnrollRequest.description,
            admin = crewEnrollRequest.admin,
            tags = TagToEntity(crewEnrollRequest.tags),
        )
        crewRepository.save(enrollCrew)

        joinCrew(enrollCrew, crewEnrollRequest.admin)
        return enrollCrew
    }

    override fun joinCrew(crew: Crew, member: Member): Crew {
        val crewMember = crewMemberRepository.save(CrewMember(crew = crew, member = member))
        crew.members.add(crewMember)
        member.joinedCrew.add(crewMember)

        return crew
    }

    fun TagToEntity(tags: List<String>): List<CrewTag>{
        // Crew Tag Count Check
        if(tags.size > 5){
            throw CrewTagCountException()
        }

        // Crew Tag Sanitization & Duplicate Check
        tags.forEach{
            if(it.length<2 || it.length>10) throw CrewTagLengthException()
            if(it.contains(" ")) throw CrewTagInvalidException()
        }
        if(tags.distinct().size!=tags.size) throw CrewTagDuplicateException()

        // Crew Tag Conversion
        val crewTags: List<CrewTag> = listOf()
        tags.forEach{
            val crewTag = CrewTag(TagName = it)
            crewTagRepository.save(crewTag)
            crewTags.plus(crewTag)
        }

        return crewTags
    }

}