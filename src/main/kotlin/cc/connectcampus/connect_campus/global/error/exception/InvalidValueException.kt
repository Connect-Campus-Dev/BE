package cc.connectcampus.connect_campus.global.error.exception

open class InvalidValueException(
    errorCode: ErrorCode = ErrorCode.INVALID_INPUT_VALUE
): BusinessException(errorCode)