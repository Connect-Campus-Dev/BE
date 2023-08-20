package cc.connectcampus.connect_campus.global.config.security

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class InvalidTokenException: SecurityException(ErrorCode.INVALID_TOKEN)