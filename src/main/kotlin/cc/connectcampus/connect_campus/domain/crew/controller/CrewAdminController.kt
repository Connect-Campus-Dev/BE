package cc.connectcampus.connect_campus.domain.crew.controller

import cc.connectcampus.connect_campus.domain.crew.domain.CrewJoinRequest
import cc.connectcampus.connect_campus.domain.crew.exception.AdminPermissionException
import cc.connectcampus.connect_campus.domain.crew.service.CrewAdminServiceV0
import cc.connectcampus.connect_campus.domain.member.domain.CustomUser
import cc.connectcampus.connect_campus.domain.member.exception.MemberNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/crew/admin")
class CrewAdminController(
    val crewAdminService: CrewAdminServiceV0,
) {
    @RequestMapping("/joinRequest/load")
    fun loadJoinRequest(@RequestParam crewId: UUID, authentication: Authentication): ResponseEntity<List<CrewJoinRequest>> {
        val adminId: UUID = (authentication.principal as CustomUser).id?: throw MemberNotFoundException()
        if(!crewAdminService.checkAdminPermission(crewId,adminId)) throw AdminPermissionException()

        return ResponseEntity.ok(crewAdminService.loadJoinRequest(crewId, adminId))
    }
    @RequestMapping("/joinRequest/permit")
    fun permitJoinRequest(@RequestParam crewId: UUID, @RequestParam joinRequestId: UUID, authentication: Authentication): ResponseEntity<UUID>{
        val adminId: UUID = (authentication.principal as CustomUser).id?: throw MemberNotFoundException()
        if(!crewAdminService.checkAdminPermission(crewId,adminId)) throw AdminPermissionException()

        return ResponseEntity.ok(crewAdminService.permitJoinRequest(joinRequestId,adminId))
    }

    @RequestMapping("/joinRequest/deny")
    fun denyJoinRequest(@RequestParam crewId: UUID, @RequestParam joinRequestId: UUID, authentication: Authentication): ResponseEntity<UUID>{
        val adminId: UUID = (authentication.principal as CustomUser).id?: throw MemberNotFoundException()
        if(!crewAdminService.checkAdminPermission(crewId,adminId)) throw AdminPermissionException()

        return ResponseEntity.ok(crewAdminService.denyJoinRequest(joinRequestId,adminId))
    }
}