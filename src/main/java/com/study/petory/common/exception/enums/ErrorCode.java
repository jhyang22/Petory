package com.study.petory.common.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements BaseCode {

	// common
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터 요청입니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청한 값에 오류가 있습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 요청입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 필요한 요청입니다."),
	NO_RESOURCE(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
	EXIST_RESOURCE(HttpStatus.CONFLICT, "중복된 데이터입니다."),
	LONG_JSON_TYPE(HttpStatus.BAD_REQUEST, "요청 형식이 잘못되었습니다. JSON 구조를 확인하세요."),
	ONLY_AUTHOR_CAN_EDIT(HttpStatus.FORBIDDEN, "작성자만 수정이 가능합니다."),
	ONLY_AUTHOR_CAN_DELETE(HttpStatus.FORBIDDEN, "작성자만 삭제가 가능합니다."),
	FAILED_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "검증에 실패했습니다."),
	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "업로드 용량을 초과했습니다."),

	// user
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	TOKEN_NOT_EXPIRED(HttpStatus.UNAUTHORIZED, "AccessToken이 만료되지 않았습니다. 재발급이 불필요합니다."),
	WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 서명입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	NO_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 없습니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),
	USER_ID_NOT_GENERATED(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 저장했지만 ID가 생성되지 않았습니다."),
	OAUTH2_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "OAuth2 인증에 이메일 정보가 없습니다."),
	ALREADY_HAS_SAME_ROLE(HttpStatus.BAD_REQUEST, "중복되는 권한입니다."),
	ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자에게 해당 권한이 존재하지 않습니다."),
	DEACTIVATED_USER(HttpStatus.FORBIDDEN, "로그인 불가합니다. 비활성화된 사용자입니다."),
	ALREADY_DEACTIVATED(HttpStatus.BAD_REQUEST, "이미 비활성화된 유저입니다."),
	USER_NOT_DEACTIVATED(HttpStatus.BAD_REQUEST, "비활성화된 유저가 아닙니다."),

	// pet
	PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫이 존재하지 않습니다."),

	// Album
	ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "앨범에 사진이 존재하지 않습니다."),
	ALBUM_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "앨범에 사진이 존재하지 않습니다."),
	ALBUM_IMAGE_SIZE_OVER(HttpStatus.BAD_REQUEST, "한 번에 등록하실 수 있는 이미지 수량을 초과했습니다."),
	ALREADY_SAVED_ALBUM_TODAY(HttpStatus.BAD_REQUEST, "오늘은 이미 데일리 앨범을 등록하였습니다."),

	// Calender
	CALENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "캘린더가 존재하지 않습니다."),

	// DailyQna
	DAILY_QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "질의 응답이 존재하지 않습니다."),
	ALREADY_WRITTEN_TODAY(HttpStatus.BAD_REQUEST, "오늘은 이미 작성하였습니다."),
	DAILY_QNA_IS_HIDDEN(HttpStatus.BAD_REQUEST, "이미 숨겨진 질문입니다."),
	DAILY_QNA_IS_NOT_HIDDEN(HttpStatus.BAD_REQUEST, "숨겨진 질문이 아닙니다."),
	DAILY_QNA_IS_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 질문입니다."),
	DAILY_QNA_IS_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제된 질문이 아닙니다."),

	// Faq
	FAQ_QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "자주 찾는 질문은 존재하지 않습니다."),

	// Feedback
	FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백이 존재하지 않습니다."),

	// OwnerBoard
	OWNER_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "글이 존재하지 않습니다."),
	OWNER_BOARD_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 게시물은 복구할 수 없습니다."),

	// Place
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소가 존재하지 않습니다."),
	JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON 처리 중 에러가 발생했습니다."),
	PLACE_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 장소입니다."),
	ALREADY_DELETED_PLACE(HttpStatus.BAD_REQUEST, "이미 삭제된 장소입니다."),
	ALREADY_INACTIVE_PLACE(HttpStatus.BAD_REQUEST, "이미 비활성화 된 장소입니다."),
	ALREADY_REPORT_PLACE(HttpStatus.BAD_REQUEST, "이미 신고한 장소입니다."),
	DUPLICATE_PLACE(HttpStatus.BAD_REQUEST, "이미 등록된 장소입니다."),
	INCONSISTENT_PLACE(HttpStatus.BAD_REQUEST, "요청사항과 장소가 일치하지 않습니다."),

	// PlaceReview
	PLACE_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
	DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성한 장소입니다."),
	REVIEW_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 리뷰입니다."),
	ALREADY_DELETED_REVIEW(HttpStatus.BAD_REQUEST, "이미 삭제된 리뷰입니다."),

	// PlaceReport
	PLACE_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고가 존재하지 않습니다."),
	ALREADY_INVALID_REPORT(HttpStatus.BAD_REQUEST, "이미 유효하지 않는 신고입니다."),

	// TradeBoard
	TRADE_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "거래 글이 존재하지 않습니다."),
	TRADE_BOARD_FORBIDDEN(HttpStatus.FORBIDDEN, "작성자만 거래글을 수정하거나 삭제할 수 있습니다."),
	TRADE_BOARD_IMAGE_OVERFLOW(HttpStatus.BAD_REQUEST, "사진은 5장까지 업로드 가능합니다."),

	// Question
	QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문이 존재하지 않습니다."),
	DATE_IS_EXIST(HttpStatus.CONFLICT, "해당 날짜에는 질문이 이미 존재합니다."),
	TODAY_QUESTION_IS_DEACTIVATED(HttpStatus.BAD_REQUEST, "관리자에 의해 비활성화된 질문입니다."),
	QUESTION_IS_DEACTIVATED(HttpStatus.BAD_REQUEST, "이미 비활성화된 질문입니다."),
	QUESTION_IS_NOT_DEACTIVATED(HttpStatus.BAD_REQUEST, "비활성화된 질문이 아닙니다."),
	QUESTION_IS_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 질문입니다."),
	QUESTION_IS_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제된 질문이 아닙니다."),

	// OwnerBoardComment
	OWNER_BOARD_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
	OWNER_BOARD_COMMENT_MISMATCH(HttpStatus.BAD_REQUEST, "해당 게시글에 존재하지 않는 댓글입니다."),

	//Chat
	CHAT_ROOM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방입니다."),
	CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
	CANNOT_SEND_MESSAGE_TO_SELF(HttpStatus.BAD_REQUEST, "자신의 게시물에 메시지를 보낼 수 없습니다."),

	// S3
	FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),
	FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 파일입니다."),
	FILE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "이미지만 업로드 할 수 있습니다."),

	//notification
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다");


	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
