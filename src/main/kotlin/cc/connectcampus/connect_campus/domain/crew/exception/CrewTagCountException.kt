package cc.connectcampus.connect_campus.domain.crew.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class CrewTagCountException: InvalidValueException(ErrorCode.CREW_TAG_COUNT_INVALID)