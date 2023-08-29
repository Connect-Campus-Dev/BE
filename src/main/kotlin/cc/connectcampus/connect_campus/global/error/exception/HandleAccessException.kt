package cc.connectcampus.connect_campus.global.error.exception

open class HandleAccessException(
        errorCode: ErrorCode = ErrorCode.HANDLE_ACCESS_DENIED
) : BusinessException(errorCode)