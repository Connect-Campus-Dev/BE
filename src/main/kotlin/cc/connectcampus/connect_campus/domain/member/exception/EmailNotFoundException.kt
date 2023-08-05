package cc.connectcampus.connect_campus.domain.member.exception

import cc.connectcampus.connect_campus.global.error.exception.EntityNotFoundException
import cc.connectcampus.connect_campus.global.error.exception.ErrorCode


class EmailNotFoundException: EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND)