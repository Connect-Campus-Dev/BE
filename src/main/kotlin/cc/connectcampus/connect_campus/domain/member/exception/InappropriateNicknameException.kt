package cc.connectcampus.connect_campus.domain.member.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class InappropriateNicknameException: InvalidValueException(ErrorCode.INAPPROPRIATE_NICKNAME) {
}