package cc.connectcampus.connect_campus.global.error.exception

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String,
) {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(405, "C002", "허용되지 않은 메소드입니다."),
    ENTITY_NOT_FOUND(400, "C003", "대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 에러입니다."),
    INVALID_TYPE_VALUE(400, "C005", "잘못된 타입입니다."),
    HANDLE_ACCESS_DENIED(403, "C006", "권한이 없습니다."),


    // Member, Auth
    EMAIL_DUPLICATION(400, "M001", "이미 사용중인 이메일입니다."),
    MEMBER_NOT_FOUND(400, "M002", "존재하지 않는 회원입니다."),
    LOGIN_INPUT_INVALID(400, "M003", "이메일 또는 비밀번호가 잘못되었습니다."),

}