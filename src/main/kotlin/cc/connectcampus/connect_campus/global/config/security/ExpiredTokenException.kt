package cc.connectcampus.connect_campus.global.config.security

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class ExpiredTokenException: SecurityException(ErrorCode.TOKEN_EXPIRED) {
}