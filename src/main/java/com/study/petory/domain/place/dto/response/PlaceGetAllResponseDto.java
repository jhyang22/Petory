package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
public class PlaceGetAllResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final String address;

	private final BigDecimal ratio;

	// private final String photoList; // 이부분도 CreateResponseDto랑 비슷하게 하면 될듯

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	public PlaceGetAllResponseDto(Long id, String placeName, String placeInfo, PlaceType placeType, String address,
		BigDecimal ratio, BigDecimal latitude, BigDecimal longitude) {
		this.id = id;
		this.placeName = placeName;
		this.placeInfo = placeInfo;
		this.placeType = placeType;
		this.address = address;
		this.ratio = ratio;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static PlaceGetAllResponseDto from(Place place) {
		return new PlaceGetAllResponseDto(
			place.getId(),
			place.getPlaceName(),
			place.getPlaceInfo(),
			place.getPlaceType(),
			place.getAddress(),
			place.getRatio(),
			place.getLatitude(),
			place.getLongitude()
		);
	}
}
