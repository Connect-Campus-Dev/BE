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
    INVALID_TOKEN(401, "C008", "유효하지 않은 토큰이에요."),
    TOKEN_EXPIRED(401, "C009", "만료된 토큰이에요."),

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
    CREW_JOIN_REQ_EXISTS(400,"CR010","가입 신청 승인 대기중입니다."),
    CREW_JOIN_NOT_FOUND(400,"CR011","존재하지 않는 가입 신청입니다."),
  
    // Chat
    CHAT_NOT_FOUND(400, "CH001", "존재하지 않는 채팅방이에요."),
    NOT_JOINED_CHAT(400, "CH002", "허용되지 않은 사용자에요."),
    CREW_MEMBER_JOINED(400, "CR010", "이미 가입한 크루에요."),

    // Attachment
    INVALID_FILE_INPUT(400, "A001", "잘못된 파일이에요."),
    INVALID_FILE_TYPE(400, "A002", "잘못된 파일 형식이에요."),
    UPLOAD_FAIL(400, "A003", "파일 업로드에 실패했어요."),

    // Post
    POST_TITLE_LENGTH_INVALID(400, "P001", "게시글 제목은 2글자 이상이여야 해요."),
    POST_CONTENT_LENGTH_INVALID(400, "P002", "게시글 내용은 2글자 이상이여야 해요."),
    POST_TAG_INVALID(400, "P003", "게시글 태그를 지정해주세요."),
    POST_TITLE_VALUE_INVALID(400, "P004", "제목이 부적절해요."),
    POST_CONTENT_VALUE_INVALID(400, "P005", "부적절한 내용이에요."),
    POST_COMMENT_LENGTH_INVALID(400, "P006","댓글 내용은 2글자 이상이여야 해요."),

}