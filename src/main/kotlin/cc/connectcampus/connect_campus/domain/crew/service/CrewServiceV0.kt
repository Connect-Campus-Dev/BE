package cc.connectcampus.connect_campus.domain.crew.service

import cc.connectcampus.connect_campus.domain.crew.domain.Crew
import cc.connectcampus.connect_campus.domain.crew.domain.CrewJoinRequest
import cc.connectcampus.connect_campus.domain.crew.domain.CrewMember
import cc.connectcampus.connect_campus.domain.crew.domain.CrewTag
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import cc.connectcampus.connect_campus.domain.crew.exception.*
import cc.connectcampus.connect_campus.domain.crew.repository.CrewJoinRequestRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewMemberRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewRepository
import cc.connectcampus.connect_campus.domain.crew.repository.CrewTagRepository
import cc.connectcampus.connect_campus.domain.member.domain.Member
import cc.connectcampus.connect_campus.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CrewServiceV0(
    val crewRepository: CrewRepository,
    val crewTagRepository: CrewTagRepository,
    val crewMemberRepository: CrewMemberRepository,
    val memberRepository: MemberRepository,
    val crewJoinRequestRepository: CrewJoinRequestRepository,
): CrewService{
    @Transactional
    override fun create(crewCreationRequest: CrewCreationRequest, adminId: UUID): Crew {
        // Crew Name Duplicate Check
        if(crewRepository.existsByName(crewCreationRequest.name)) throw CrewNameDuplicateException()

        val requestedAdmin: Member = memberRepository.findById(adminId)!!

        val creationCrew = Crew(
            name = crewCreationRequest.name,
            description = crewCreationRequest.description,
            admin = requestedAdmin,
            tags = TagToEntity(crewCreationRequest.tags),
        )
        val createdCrew = crewRepository.save(creationCrew)

        return createdCrew
    }

    @Transactional
    override fun joinRequest(crewId: UUID, memberId: UUID): Crew {
        // Crew Existence Check
        val crew = crewRepository.findById(crewId)?: throw CrewNotFoundException()

        // Request Existence Check
        if(crewJoinRequestRepository.existsByCrewIdAndMemberId(crew.id!!, memberId)) throw CrewJoinFoundException()

        // Member Joined Crew Check
        if(crewMemberRepository.existsByCrewIdAndMemberId(crewId, memberId)) throw CrewMemberJoinedException()

        val member = memberRepository.findById(memberId)!!
        crewJoinRequestRepository.save(CrewJoinRequest(crew = crew, member = member))
        return crew
    }

    @Transactional
    override fun join(crewId: UUID, memberId: UUID): Boolean{
        val member = memberRepository.findById(memberId)!!
        val crew = crewRepository.findById(crewId)!!
        val crewMember = CrewMember(crew = crew, member = member)
        crewMemberRepository.save(crewMember)
        crew.members.add(crewMember)
        member.joinedCrew.add(crewMember)
        return true
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