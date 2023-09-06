package cc.connectcampus.connect_campus.domain.post.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class PostSearchInvalidException: InvalidValueException(ErrorCode.POST_SEARCH_INVALID)