package cc.connectcampus.connect_campus.domain.post.exception

import cc.connectcampus.connect_campus.global.error.exception.ErrorCode
import cc.connectcampus.connect_campus.global.error.exception.InvalidValueException

class PostCommentLengthInvalid : InvalidValueException(ErrorCode.POST_COMMENT_LENGTH_INVALID)