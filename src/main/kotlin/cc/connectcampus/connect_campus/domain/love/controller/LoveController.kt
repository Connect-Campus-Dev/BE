package cc.connectcampus.connect_campus.domain.love.controller

import cc.connectcampus.connect_campus.domain.love.dto.request.LoveProfileCreationRequest
import cc.connectcampus.connect_campus.domain.love.dto.request.PartyCreationRequest
import cc.connectcampus.connect_campus.domain.love.dto.response.LoveProfileResponse
import cc.connectcampus.connect_campus.domain.love.service.LoveProfileService
import cc.connectcampus.connect_campus.global.CommonResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/love")
class LoveController(
    private val loveProfileService: LoveProfileService,
) {
    @PostMapping("/profile")
    fun createLoveProfile(
        @Valid @RequestBody loveProfileCreationRequest: LoveProfileCreationRequest
    ): CommonResponse {
        return loveProfileService.createLoveProfile(loveProfileCreationRequest)
    }

    @GetMapping("/profile/{id}")
    fun getLoveProfile(
        @PathVariable id: String
    ): LoveProfileResponse {
        return loveProfileService.getLoveProfile(id)
    }


}