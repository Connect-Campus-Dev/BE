package cc.connectcampus.connect_campus.domain.attachment.exception

import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class UploadFailException: BusinessException(ErrorCode.UPLOAD_FAIL)