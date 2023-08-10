package cc.connectcampus.connect_campus.domain.crew.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class CrewDescriptionException: InvalidValueException(ErrorCode.CREW_DESCRIPTION_LENGTH_INVALID) {
}