package cc.connectcampus.connect_campus.domain.crew.exception

import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class CrewNotFoundException: EntityNotFoundException(ErrorCode.CREW_NOT_FOUND) {
}