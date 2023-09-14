package cc.connectcampus.connect_campus.domain.crew.exception

import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class CrewJoinFoundException: BusinessException(ErrorCode.CREW_JOIN_REQ_EXISTS){
}