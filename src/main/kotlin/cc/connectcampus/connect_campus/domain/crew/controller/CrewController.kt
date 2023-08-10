package cc.connectcampus.connect_campus.domain.crew.controller

import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewCreationRequest
import cc.connectcampus.connect_campus.domain.crew.dto.request.CrewJoinRequest
import cc.connectcampus.connect_campus.domain.crew.dto.response.CrewCreationResponse
import cc.connectcampus.connect_campus.domain.crew.dto.response.CrewJoinResponse
import cc.connectcampus.connect_campus.domain.crew.service.CrewServiceV0
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/crew")
class CrewController(
    val crewService: CrewServiceV0,
) {

    @RequestMapping("/create")
    fun crewEnroll(@RequestBody @Valid crewCreationRequest: CrewCreationRequest) : CrewCreationResponse {
        return CrewCreationResponse(crewService.create(crewCreationRequest).name)
    }
    @RequestMapping("/join")
    fun crewJoin(@RequestBody @Valid crewJoinRequest: CrewJoinRequest): CrewJoinResponse {
        return CrewJoinResponse(crewService.join(crewJoinRequest.crewId,crewJoinRequest.memberId).name)
    }
}