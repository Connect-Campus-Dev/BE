package cc.connectcampus.connect_campus.domain.crew.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class CrewNameLengthException: InvalidValueException(ErrorCode.CREW_NAME_LENGTH_INVALID)