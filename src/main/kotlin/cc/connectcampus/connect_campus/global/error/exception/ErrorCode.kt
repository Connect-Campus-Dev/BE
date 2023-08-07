package cc.connectcampus.connect_campus.global.error.exception

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ErrorCode(
    val status: Int,
    val code: String,
    val message: String,
) {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력이에요."),
    METHOD_NOT_ALLOWED(405, "C002", "허용되지 않은 메소드에요."),
    ENTITY_NOT_FOUND(400, "C003", "대상을 찾을 수 없어요."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 에러에요. 잠시 후, 다시 시도해주세요."),
    INVALID_TYPE_VALUE(400, "C005", "잘못된 타입이에요."),
    HANDLE_ACCESS_DENIED(403, "C006", "권한이 없어요."),
    NOT_VERIFIED(403, "C007", "인증되지 않은 사용자에요."),

    // Member, Auth
    EMAIL_DUPLICATION(400, "M001", "이미 사용중인 이메일이에요."),
    MEMBER_NOT_FOUND(400, "M002", "존재하지 않는 회원이에요."),
    LOGIN_INPUT_INVALID(400, "M003", "이메일 또는 비밀번호가 잘못되었어요."),
    NICKNAME_DUPLICATION(400, "M004", "이미 사용중인 닉네임이에요."),
    TIMEOUT(400, "M005", "인증 시간이 초과되었어요."),
    CODE_INVALID(400, "M006", "인증 코드가 잘못되었어요."),
    INAPPROPRIATE_NICKNAME(400, "M007", "부적절한 닉네임이에요."),
    NOT_ALLOWED_NICKNAME_LENGTH(400, "M008", "닉네임은 2자 이상, 10자 이하여야 해요."),
    EMAIL_INVALID(400, "M009", "학교 이메일이 아니에요."),
}