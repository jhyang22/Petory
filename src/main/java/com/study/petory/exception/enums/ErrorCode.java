package com.study.petory.exception.enums;

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
	ONLY_AUTHOR_CAN_EDIT (HttpStatus.FORBIDDEN, "작성자만 수정이 가능합니다."),
	ONLY_AUTHOR_CAN_DELETE (HttpStatus.FORBIDDEN, "작성자만 삭제가 가능합니다."),

	// user
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),

	// pet
	PET_NOT_FOUND(HttpStatus.NOT_FOUND, "펫이 존재하지 않습니다."),

	// Album
	ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "앨범에 사진이 존재하지 않습니다."),

	// Calender
	CALENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "캘린더가 존재하지 않습니다."),

	// DailyQna
	DAILY_QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "질의 응답이 존재하지 않습니다."),

	// Faq
	FAQ_QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "자주 찾는 질문은 존재하지 않습니다."),

	// Feedback
	FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "피드백이 존재하지 않습니다."),

	// OwnerBoard
	OWNER_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "글이 존재하지 않습니다."),
	OWNER_BOARD_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 게시물은 복구할 수 없습니다."),

	// Place
	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소가 존재하지 않습니다."),

	// PlaceReview
	PLACE_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
	DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성한 장소입니다."),
	REVIEW_NOT_DELETED(HttpStatus.BAD_REQUEST, "삭제되지 않은 리뷰입니다."),
	ALREADY_DELETED_REVIEW(HttpStatus.BAD_REQUEST, "이미 삭제된 리뷰입니다."),

	// TradeBoard
	TRADE_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "거래 글이 존재하지 않습니다."),
	TRADE_BOARD_FORBIDDEN(HttpStatus.FORBIDDEN, "작성자만 거래글을 수정하거나 삭제할 수 있습니다."),

	// Question
	QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문이 존재하지 않습니다."),
	DATE_IS_EXIST(HttpStatus.CONFLICT, "해당 날짜에는 질문이 이미 존재합니다."),
	QUESTION_IS_DEACTIVATED(HttpStatus.BAD_REQUEST, "이미 비활성화된 질문입니다."),
	QUESTION_IS_NOT_DEACTIVATED(HttpStatus.BAD_REQUEST, "비활성화된 질문이 아닙니다."),
	TODAY_QUESTION_IS_DEACTIVATED(HttpStatus.BAD_REQUEST, "일시적인 오류가 발생했습니다."),

	// OwnerBoardComment
	OWNER_BOARD_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
	OWNER_BOARD_COMMENT_MISMATCH(HttpStatus.BAD_REQUEST, "해당 게시글에 존재하지 않는 댓글입니다."),

	//Chat
	CHAT_ROOM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방입니다."),
	CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),

	// S3
	FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),
	FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 파일입니다.")

		;

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
