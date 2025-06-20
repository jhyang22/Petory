package com.study.petory.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {

	private String email;
	private String nickname;
	private String name;        // UserPrivateInfo
	private String mobileNum;   // UserPrivateInfo
}
