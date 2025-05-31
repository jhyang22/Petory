package com.study.petory.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.PlaceType;

public interface PlaceService {
	PlaceCreateResponseDto savePlace(PlaceCreateRequestDto requestDto);

	Page<PlaceGetResponseDto> findAllPlace(String placeName, PlaceType placeType, Pageable pageable);

	PlaceGetResponseDto findByPlaceId(Long placeId);

	PlaceUpdateResponseDto updatePlace(Long placeId, PlaceUpdateRequestDto requestDto);

	void deletePlace(Long placeId, PlaceStatusChangeRequestDto requestDto);

	void restorePlace(Long placeId, PlaceStatusChangeRequestDto requestDto);
}
