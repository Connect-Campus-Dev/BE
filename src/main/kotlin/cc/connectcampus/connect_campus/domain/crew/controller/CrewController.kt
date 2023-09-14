package cc.connectcampus.connect_campus.domain.crew.controller

import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest

import cc.connectcampus.connect_campus.domain.crew.service.CrewServiceV0
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/crew")
class CrewController(
    val crewService: CrewServiceV0,
) {

    @RequestMapping("/create")
    fun crewEnroll(@RequestBody @Valid crewCreationRequest: CrewCreationRequest, authentication: Authentication) : ResponseEntity<String> {
        val adminId: UUID = (authentication.principal as CustomUser).id?: throw MemberNotFoundException()
        val createdCrew = crewService.create(crewCreationRequest,adminId)
        // 생성 후 admin 가입 처리
        crewService.join(createdCrew.id!!, adminId)

        return ResponseEntity.ok(createdCrew.name)
    }

    @RequestMapping("/join")
    fun crewJoin(@RequestParam crewId: UUID, authentication: Authentication): ResponseEntity<String> {
        val memberId: UUID = (authentication.principal as CustomUser).id?: throw MemberNotFoundException()
        return ResponseEntity.ok(crewService.joinRequest(crewId, memberId).name)
    }


}