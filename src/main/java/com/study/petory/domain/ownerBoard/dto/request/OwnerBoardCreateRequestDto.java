package com.study.petory.domain.ownerBoard.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OwnerBoardCreateRequestDto {

	@NotBlank(message = "제목은 필수 입력값 입니다.")
	@Size(max = 30, message = "최대 30자까지 입력할 수 있습니다.")
	private String title;

	@NotBlank(message = "내용은 필수 입력값 입니다.")
	private String content;

}
