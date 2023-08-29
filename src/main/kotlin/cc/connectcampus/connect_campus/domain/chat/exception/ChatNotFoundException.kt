package cc.connectcampus.connect_campus.domain.chat.exception

import cc.connectcampus.connect_campus.global.error.exception.BusinessException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class ChatNotFoundException: BusinessException(ErrorCode.CHAT_NOT_FOUND)