package cc.connectcampus.connect_campus.global.config.security

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

open class SecurityException(val errorCode: ErrorCode = ErrorCode.NOT_VERIFIED): RuntimeException()