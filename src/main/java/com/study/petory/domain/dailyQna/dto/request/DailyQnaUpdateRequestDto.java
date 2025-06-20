package com.study.petory.domain.dailyQna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyQnaUpdateRequestDto {

	@NotBlank
	@Size(max = 255, message = "85글자 이하로 입력해주세요.")
	private String answer;
}
