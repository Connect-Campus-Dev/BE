package cc.connectcampus.connect_campus.domain.member.exception

import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode

class MemberNotFoundException: EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND)