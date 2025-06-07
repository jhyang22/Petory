package com.study.petory.domain.ownerBoard.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.service.OwnerBoardCommentService;
import com.study.petory.domain.ownerBoard.service.OwnerBoardImageService;
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner-boards")
public class OwnerBoardController {

	private final OwnerBoardService ownerBoardService;
	private final OwnerBoardCommentService ownerBoardCommentService;
	private final OwnerBoardImageService ownerBoardImageService;

	/**
	 * 게시글 생성
	 * @param dto 제목, 내용 //사진 추가 예정
	 * @return id, 제목, 내용, 생성일
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<OwnerBoardCreateResponseDto>> createOwnerBoard(
		@RequestPart @Valid OwnerBoardCreateRequestDto dto,
		@RequestPart(required = false) List<MultipartFile> images) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardService.saveOwnerBoard(dto, images));
	}

	// 사진 삭제
	@DeleteMapping("/{boardId}/images/{imageId}")
	public ResponseEntity<CommonResponse<Void>> deleteImage(
		@PathVariable Long boardId,
		@PathVariable Long imageId) {
		ownerBoardService.deleteImage(boardId, imageId);

		return CommonResponse.of(SuccessCode.OK);
	}

	/**
	 * 게시글 전체 조회
	 * @param title 제목 검색 가능(nullable)
	 * @param page 조회하려는 페이지 위치
	 * @return List형식의 게시글 반환
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Page<OwnerBoardGetAllResponseDto>>> getOwnerBoardsAll(
		@RequestParam(required = false) String title,
		@RequestParam(defaultValue = "1") int page) {

		return CommonResponse.of(SuccessCode.REQUESTED, ownerBoardService.findAllOwnerBoards(title, page));
	}

	/**
	 * 게시글 단건 조회
	 * @param boardId 주인커뮤니티 게시글 ID
	 * @return 게시글 id,제목,내용,작성일,수정일, 댓글리스트(오래된순 10개) //사진리스트 추가 예정
	 */
	@GetMapping("/{boardId}")
	public ResponseEntity<CommonResponse<OwnerBoardGetResponseDto>> getOwnerBoard(
		@PathVariable Long boardId) {

		return CommonResponse.of(SuccessCode.REQUESTED, ownerBoardService.findOwnerBoard(boardId));
	}

	/**
	 * 게시글 수정
	 * @param boardId 수정할 주인커뮤니티 게시글 ID
	 * @param dto 제목, 내용 // 사진 추가 예정
	 * @return 수정된 OwnerBoard 반환
	 */
	@PatchMapping("/{boardId}")
	public ResponseEntity<CommonResponse<OwnerBoardUpdateResponseDto>> updateOwnerBoard(
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardUpdateRequestDto dto) {

		return CommonResponse.of(SuccessCode.REQUESTED, ownerBoardService.updateOwnerBoard(boardId, dto));
	}

	/**
	 * 게시글 삭제
	 * @param boardId 삭제할 게시글 ID
	 * @return NO_CONTENT 성공코드 반환
	 */
	@DeleteMapping("/{boardId}")
	public ResponseEntity<CommonResponse<Void>> deleteOwnerBoard(
		@PathVariable Long boardId) {
		ownerBoardService.deleteOwnerBoard(boardId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 게시글 복구
	 * @param boardId 복구할 게시글 ID
	 * @return RESTORE 성공코드 반환
	 */
	@PatchMapping("/{boardId}/restore")
	public ResponseEntity<CommonResponse<Void>> restoreOwnerBoard(
		@PathVariable Long boardId) {
		ownerBoardService.restoreOwnerBoard(boardId);

		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 주인커뮤니티 댓글 생성
	 * @param boardId 게시글 Id
	 * @param dto 내용 작성
	 * @return 댓글 Id, 내용, 생성일, 작성자 ID 반환
	 */
	@PostMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<OwnerBoardCommentCreateResponseDto>> createOwnerBoardComment(
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardCommentCreateRequestDto dto) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardCommentService.saveOwnerBoardComment(boardId, dto));
	}

	/**
	 * 게시글의 댓글 전체 조회(페이징)
	 * @param page 댓글 페이지
	 * @return Page size 10, 생성일 기준 오름차순 정렬
	 */
	@GetMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<Page<OwnerBoardCommentGetResponseDto>>> getOwnerBoardCommentsAll(
		@PathVariable long boardId,
		@RequestParam(defaultValue = "1") int page) {

		return CommonResponse.of(SuccessCode.REQUESTED, ownerBoardCommentService.findAllOwnerBoardComments(boardId, page));
	}

	/**
	 * 주인커뮤니티 댓글 수정
	 * @param boardId 댓글이 속한 게시글 ID
	 * @param commentId 댓글 ID
	 * @param dto 수정 내용
	 * @return 댓글 ID, 수정된 내용, 수정일, 작성자 ID
	 */
	@PatchMapping("/{boardId}/comments/{commentId}")
	public ResponseEntity<CommonResponse<OwnerBoardCommentUpdateResponseDto>> updateOwnerBoardComment(
		@PathVariable Long boardId,
		@PathVariable Long commentId,
		@Valid @RequestBody OwnerBoardCommentUpdateRequestDto dto
	) {

		return CommonResponse.of(SuccessCode.REQUESTED,
			ownerBoardCommentService.updateOwnerBoardComment(boardId, commentId, dto));
	}

	/**
	 * 주인커뮤니티 댓글 삭제
	 * @param boardId 댓글이 속한 게시글 ID
	 * @param commentId 댓글 ID
	 * @return NO_CONTENT 성공코드 반환
	 */
	@DeleteMapping("/{boardId}/comments/{commentId}")
	public ResponseEntity<CommonResponse<Void>> deleteOwnerBoardComment(
		@PathVariable Long boardId,
		@PathVariable Long commentId
	) {
		ownerBoardCommentService.deleteOwnerBoardComment(boardId, commentId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

}
