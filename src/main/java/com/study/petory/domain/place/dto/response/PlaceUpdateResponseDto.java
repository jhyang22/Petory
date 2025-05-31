package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
public class PlaceUpdateResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final BigDecimal ratio;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private final LocalDateTime updatedAt;

	private PlaceUpdateResponseDto(Place place) {
		this.id = place.getId();
		this.placeName = place.getPlaceName();
		this.placeInfo = place.getPlaceInfo();
		this.placeType = place.getPlaceType();
		this.ratio = place.getRatio();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.updatedAt = place.getUpdatedAt();
	}

	public static PlaceUpdateResponseDto from(Place place) {
		return new PlaceUpdateResponseDto(place);
	}
}
