package com.study.petory.domain.place.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	// placeName, placeType이 둘 다 있는 경우. 두 가지의 필터를 모두 포함한 조회
	Page<PlaceGetResponseDto> findAllByPlaceNameContainingAndPlaceType(String placeName, PlaceType placeType, Pageable pageable);

	// placeName이 존재하는 경우 placeName 중 일부만 입력되는 경우에도 조회 가능
	Page<PlaceGetResponseDto> findAllByPlaceNameContaining(String placeName, Pageable pageable);

	// placeType이 존재하는 경우 placeType 기준 조회
	Page<PlaceGetResponseDto> findAllByPlaceType(PlaceType placeType, Pageable pageable);

	// 전체 조회
	@Query("SELECT new com.study.petory.domain.place.dto.response.PlaceGetResponseDto(p.id, p.placeName, p.placeInfo, p.placeType, p.ratio, p.latitude, p.longitude) "
		+ "FROM Place p")
	Page<PlaceGetResponseDto> findAllPlace(Pageable pageable);

	default Place findByIdOrElseThrow(long id) {
		return findById(id).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
	}
}
