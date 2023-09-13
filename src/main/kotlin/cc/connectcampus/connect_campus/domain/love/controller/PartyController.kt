package cc.connectcampus.connect_campus.domain.love.controller

import cc.connectcampus.connect_campus.domain.love.dto.request.PartyCreationRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/party")
class PartyController {

    //파티
    @PostMapping
    fun createParty(@RequestBody partyCreationRequest: PartyCreationRequest) {

    }

    @GetMapping("/{id}")
    fun getParty(@PathVariable id: String) {

    }

    @PostMapping("/{id}/join")
    fun joinParty(@PathVariable id: String) {

    }

    @DeleteMapping("/{id}/leave")
    fun leaveParty(@PathVariable id: String) {

    }

    @PostMapping("/{id}/accept")
    fun acceptParty(@PathVariable id: String) {

    }

    @PostMapping("/{id}/reject")
    fun rejectParty(@PathVariable id: String) {

    }

    @DeleteMapping("/{id}")
    fun deleteParty(@PathVariable id: String) {

    }
}