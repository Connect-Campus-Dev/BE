package cc.connectcampus.connect_campus.domain.attachment.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException


class InvalidFileTypeException: InvalidValueException(ErrorCode.INVALID_FILE_TYPE)