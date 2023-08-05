package cc.connectcampus.connect_campus.global.error.exception

open class EntityNotFoundException(
    errorCode: ErrorCode = ErrorCode.ENTITY_NOT_FOUND
) : BusinessException(errorCode)