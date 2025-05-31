package com.study.petory.domain.place.dto.request;

import java.math.BigDecimal;

import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceCreateRequestDto {

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final String address;

	private final BigDecimal latitude;

	private final BigDecimal longitude;
}
