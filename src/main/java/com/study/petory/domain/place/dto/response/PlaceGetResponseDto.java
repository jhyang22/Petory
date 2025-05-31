package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
public class PlaceGetResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final BigDecimal ratio;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private PlaceGetResponseDto(Long id, String placeName, String placeInfo, PlaceType placeType, BigDecimal ratio,
		BigDecimal latitude, BigDecimal longitude) {
		this.id = id;
		this.placeName = placeName;
		this.placeInfo = placeInfo;
		this.placeType = placeType;
		this.ratio = ratio;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static PlaceGetResponseDto from(Place place) {
		return new PlaceGetResponseDto(
			place.getId(),
			place.getPlaceName(),
			place.getPlaceInfo(),
			place.getPlaceType(),
			place.getRatio(),
			place.getLatitude(),
			place.getLongitude()
		);
	}
}
