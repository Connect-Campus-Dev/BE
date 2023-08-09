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

    // Crew
    CREW_NOT_FOUND(400, "CR001", "존재하지 않는 크루에요."),
    CREW_NAME_DUPLICATION(400, "CR002", "이미 사용중인 크루 이름이에요."),
    CREW_DESCRIPTION_LENGTH_INVALID(400, "CR003", "크루 소개는 100자 이하여야 해요."),
    CREW_NAME_INVALID(400, "CR004", "크루 이름이 부적절해요."),
    CREW_TAG_INVALID(400, "CR005", "크루 태그가 부적절해요."),
    CREW_NAME_LENGTH_INVALID(400, "CR006", "크루 이름은 2자 이상, 10자 이하여야 해요."),
    CREW_TAG_LENGTH_INVALID(400, "CR007", "크루 태그는 2자 이상, 10자 이하여야 해요."),
    CREW_TAG_COUNT_INVALID(400,"CR008", "크루 태그는 1개 이상, 5개 이하여야 해요."),
    CREW_TAG_DUPLICATION(400, "CR009", "크루 태그가 중복되었어요."),
}