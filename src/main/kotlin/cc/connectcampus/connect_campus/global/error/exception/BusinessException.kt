package cc.connectcampus.connect_campus.global.error.exception

open class BusinessException(val errorCode: ErrorCode): RuntimeException()
