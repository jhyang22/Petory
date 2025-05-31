package com.study.petory.domain.place.entity;

import lombok.Getter;

@Getter
public enum Status {
	ACTIVE("영업중"),
	INACTIVE("영업 중지"),
	DELETED("폐업"); // -> 잘못 신고가 들어와서 폐업으로 오인할 수도 있어서 일단 소프트 딜리트 하고 일정 기간 이후에 하드 딜리트 되도록!

	private final String displayName;

	Status(String displayName) {
		this.displayName = displayName;
	}
}
