package com.study.petory.domain.ownerBoard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardCommentRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardImageRepository;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerBoardServiceImpl implements OwnerBoardService {
	private final OwnerBoardRepository ownerBoardRepository;
	private final UserRepository userRepository;
	private final OwnerBoardCommentRepository ownerBoardCommentRepository;
	private final OwnerBoardImageService ownerBoardImageService;
	private final OwnerBoardImageRepository ownerBoardImageRepository;

	// ownerBoardId로 OwnerBoard 조회
	@Override
	public OwnerBoard findOwnerBoardById(Long boardId) {
		return ownerBoardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NO_RESOURCE));
	}

	// 게시글 생성
	@Override
	@Transactional
	public OwnerBoardCreateResponseDto saveOwnerBoard(OwnerBoardCreateRequestDto dto, List<MultipartFile> images) {
		User user = userRepository.findById(1L).orElseThrow(); // 추후 토큰값으로 수정

		OwnerBoard ownerBoard = OwnerBoard.builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.user(user)
			.build();

		ownerBoardRepository.save(ownerBoard);

		List<String> urls = new ArrayList<>();
		if (images != null && !images.isEmpty()) {
			urls = ownerBoardImageService.uploadAndSaveAll(images, ownerBoard);
		}

		return OwnerBoardCreateResponseDto.of(ownerBoard, urls);
	}

	// 게시글 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<OwnerBoardGetAllResponseDto> findAllOwnerBoards(String title, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		PageRequest pageRequest = PageRequest.of(adjustedPage, 5, Sort.by("createdAt").descending());

		Page<OwnerBoard> boards;
		if (title != null) {
			boards = ownerBoardRepository.findByTitleContaining(title, pageRequest);
		} else {
			boards = ownerBoardRepository.findAll(pageRequest);
		}

		return boards.map(OwnerBoardGetAllResponseDto::from);
	}

	// 게시글 단건 조회
	@Override
	@Transactional(readOnly = true)
	public OwnerBoardGetResponseDto findOwnerBoard(Long boardId) {
		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		List<OwnerBoardComment> initialComments = ownerBoardCommentRepository.findTop10ByOwnerBoardIdOrderByCreatedAt(
			boardId);

		List<OwnerBoardCommentGetResponseDto> commentsList = initialComments.stream()
			.map(OwnerBoardCommentGetResponseDto::from)
			.toList();

		return OwnerBoardGetResponseDto.of(ownerBoard, commentsList);
	}

	// 게시글 수정
	@Override
	@Transactional
	public OwnerBoardUpdateResponseDto updateOwnerBoard(Long boardId, OwnerBoardUpdateRequestDto requestDto) {
		// 본인 작성 글인지 검증 로직 추가

		OwnerBoard ownerBoard = findOwnerBoardById(boardId);

		if (requestDto.getTitle() != null) {
			ownerBoard.updateTitle(requestDto.getTitle());
		}
		if (requestDto.getContent() != null) {
			ownerBoard.updateContent(requestDto.getContent());
		}

		return OwnerBoardUpdateResponseDto.from(ownerBoard);
	}

	// 게시글 삭제
	@Override
	@Transactional
	public void deleteOwnerBoard(Long boardId) {
		// 본인 작성 글인지 검증 로직 추가

		OwnerBoard ownerBoard = findOwnerBoardById(boardId);
		ownerBoard.deactivateEntity();
	}

	// 게시글 복구
	@Override
	@Transactional
	public void restoreOwnerBoard(Long boardId) {
		// 관리자 권한 검증 로직 추가

		OwnerBoard ownerBoard = ownerBoardRepository.findByIdIncludingDeleted(boardId)
			.orElseThrow(() -> new CustomException(ErrorCode.NO_RESOURCE));

		if (ownerBoard.getDeletedAt() == null) {
			throw new CustomException(ErrorCode.OWNER_BOARD_NOT_DELETED);
		}

		ownerBoard.restoreEntity();
	}

	// 게시글 사진 삭제
	@Override
	public void deleteImage(Long boardId, Long imageId) {
		findOwnerBoardById(boardId);
		ownerBoardImageService.deleteImage(imageId);
	}

}
