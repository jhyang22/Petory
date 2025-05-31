package com.study.petory.domain.place.entity;

import lombok.Getter;

@Getter
public enum PlaceType {
	CAFE("카페"),
	RESTAURANT("음식점"),
	HOSPITAL("병원"),
	STORE("애견 용품점"),
	BEAUTY("애견 미용실"),
	SCHOOL("애견 유치원");

	private final String displayName;

	PlaceType(String displayName) {
		this.displayName = displayName;
	}
}
