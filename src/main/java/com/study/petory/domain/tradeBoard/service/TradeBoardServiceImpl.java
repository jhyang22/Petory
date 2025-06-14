package com.study.petory.domain.tradeBoard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardImage;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.repository.TradeBoardQueryRepository;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeBoardServiceImpl implements TradeBoardService {

	private final TradeBoardRepository tradeBoardRepository;
	private final UserRepository userRepository;
	private final TradeBoardImageService tradeBoardImageService;
	private final TradeBoardQueryRepository tradeBoardQueryRepository;

	public User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	//tradeBoardId로 tradeBoard 조회
	public TradeBoard findTradeBoard(Long tradeBoardId) {
		return tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));
	}

	public List<String> imageToUrlList(TradeBoard tradeBoard){
		if (!tradeBoard.getImages().isEmpty()) {
			return tradeBoard.getImages().stream()
				.map(TradeBoardImage::getUrl)
				.toList();
		}
		return List.of();
	}

	//게시글의 유저가 맞는지 검증
	public void isOwner(Long userId, TradeBoard tradeBoard) {
		if (!tradeBoard.isOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
	}

	//게시글 생성
	@Override
	@Transactional
	public TradeBoardCreateResponseDto saveTradeBoard(Long userId, TradeBoardCreateRequestDto requestDto, List<MultipartFile> images) {

		//나중에 토큰으로 값을 받아올 예정
		User user = findUser(userId);

		TradeBoard tradeBoard = TradeBoard.builder()
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.price(requestDto.getPrice())
			.user(user)
			.build();

		tradeBoardRepository.save(tradeBoard);


		List<String> urls = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			urls = tradeBoardImageService.uploadAndSaveAll(images, tradeBoard);
		}

		return new TradeBoardCreateResponseDto(tradeBoard, urls);
	}

	//게시글 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<TradeBoardGetAllResponseDto> findAllTradeBoard(TradeCategory category, Pageable pageable) {

		Page<TradeBoard> tradeBoards = tradeBoardQueryRepository.findAll(category, pageable);

		return tradeBoards.map(TradeBoardGetAllResponseDto::new);
	}

	//게시글 단건 조회
	@Override
	@Transactional(readOnly = true)
	public TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId) {

		TradeBoard tradeBoard = findTradeBoard(tradeBoardId);
		List<String> urls = imageToUrlList(tradeBoard);

		return new TradeBoardGetResponseDto(tradeBoard, urls);
	}

	// 유저별 게시글 조회
	@Override
	@Transactional(readOnly = true)
	public Page<TradeBoardGetAllResponseDto> findByUser(Long userId, Pageable pageable) {

		Page<TradeBoard> tradeBoards = tradeBoardQueryRepository.findByUserId(userId, pageable);

		return tradeBoards.map(TradeBoardGetAllResponseDto::new);
	}

	//게시글 수정
	@Override
	@Transactional
	public TradeBoardUpdateResponseDto updateTradeBoard(Long userId, Long tradeBoardId, TradeBoardUpdateRequestDto requestDto) {

		TradeBoard tradeBoard = findTradeBoard(tradeBoardId);

		isOwner(userId, tradeBoard);

		tradeBoard.updateTradeBoard(requestDto);

		return new TradeBoardUpdateResponseDto(tradeBoard);
	}

	//게시글 상태 업데이트
	@Override
	@Transactional
	public void updateTradeBoardStatus(Long userId, Long tradeBoardId, TradeBoardStatus status) {

		//토큰 값으로 변경 예정
		User loginUser = findUser(userId);

		TradeBoard tradeBoard = findTradeBoard(tradeBoardId);

		isOwner(userId, tradeBoard);

		tradeBoard.updateStatus(status);
	}

	//사진 추가
	@Override
	@Transactional
	public void addImages(Long userId, Long tradeBoardId, List<MultipartFile> images) {

		TradeBoard tradeBoard = findTradeBoard(tradeBoardId);

		isOwner(userId, tradeBoard);

		List<TradeBoardImage> imageEntities = tradeBoardImageService.uploadAndReturnEntities(images, tradeBoard);

		for (TradeBoardImage image : imageEntities) {
			tradeBoard.addImage(image);
		}
	}

	//게시글 내 사진 삭제
	@Override
	@Transactional
	public void deleteImage(Long userId, Long tradeBoardId, Long imageId) {

		TradeBoard tradeBoard = findTradeBoard(tradeBoardId);

		if (!tradeBoard.isOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		TradeBoardImage image = tradeBoardImageService.findImageById(imageId);

		tradeBoardImageService.deleteImageInternal(image);
	}
}