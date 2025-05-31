package com.study.petory.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	// 장소 등록
	@Override
	@Transactional
	public PlaceCreateResponseDto savePlace(PlaceCreateRequestDto requestDto) {

		User user = userRepository.findById(1L).orElseThrow();

		Place place = Place.builder()
			.user(user)
			.placeName(requestDto.getPlaceName())
			.placeInfo(requestDto.getPlaceInfo())
			.placeType(requestDto.getPlaceType())
			// .ratio()
			.address(requestDto.getAddress())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.build();

		placeRepository.save(place);

		return PlaceCreateResponseDto.from(place);
	}

	// 전체 장소 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PlaceGetResponseDto> findAllPlace(String placeName, PlaceType placeType, Pageable pageable) {

		// placeName, placeType이 둘 다 있는 경우. 두 가지의 필터를 모두 포함한 조회
		if (placeName != null && placeType != null) {
			return placeRepository.findAllByPlaceNameContainingAndPlaceType(placeName, placeType, pageable);
		}

		// placeName이 존재하는 경우 placeName 중 일부만 입력되는 경우에도 조회 가능
		if (placeName != null) {
			return placeRepository.findAllByPlaceNameContaining(placeName, pageable);
		}

		// placeType이 존재하는 경우 placeType 기준 조회
		if (placeType != null) {
			return placeRepository.findAllByPlaceType(placeType, pageable);
		}

		// 전체 조회
		return placeRepository.findAllPlace(pageable);
	}

	// 특정 장소 조회
	@Override
	public PlaceGetResponseDto findByPlaceId(Long placeId) {

		Place findPlace = placeRepository.findByIdOrElseThrow(placeId);

		return PlaceGetResponseDto.from(findPlace);
	}

	// 장소 수정
	@Override
	@Transactional
	public PlaceUpdateResponseDto updatePlace(Long placeId, PlaceUpdateRequestDto requestDto) {

		Place findPlace = placeRepository.findByIdOrElseThrow(placeId);

		findPlace.updatePlace(requestDto);

		return PlaceUpdateResponseDto.from(findPlace);
	}

	// 장소 삭제
	@Override
	@Transactional
	public void deletePlace(Long placeId, PlaceStatusChangeRequestDto requestDto) {
		Place findPlace = placeRepository.findByIdOrElseThrow(placeId);

		findPlace.deactivateEntity();
		findPlace.updateStatus(requestDto);
	}

	// 삭제된 장소 복구
	@Override
	@Transactional
	public void restorePlace(Long placeId, PlaceStatusChangeRequestDto requestDto) {

		Place findPlace = placeRepository.findByIdOrElseThrow(placeId);

		findPlace.restoreEntity();
		findPlace.updateStatus(requestDto);
	}
}
