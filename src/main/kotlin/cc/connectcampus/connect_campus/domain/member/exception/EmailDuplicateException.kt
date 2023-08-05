package cc.connectcampus.connect_campus.domain.member.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class EmailDuplicateException: InvalidValueException(ErrorCode.EMAIL_DUPLICATION)