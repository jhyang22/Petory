package com.study.petory.exception.enums;

import org.springframework.http.HttpStatus;

public enum SuccessCode implements BaseCode {

	// Common
	OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
	CREATED(HttpStatus.CREATED, "요청이 성공적으로 생성되었습니다."),
	NO_CONTENT(HttpStatus.NO_CONTENT, "요청이 성공적으로 처리되었습니다."),
	ROLLBACK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	SuccessCode(HttpStatus httpStatus, String message) {
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
