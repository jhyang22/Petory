package com.study.petory.domain.ownerBoard.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
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
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner-boards")
public class OwnerBoardController {

	private final OwnerBoardService ownerBoardService;
	private final OwnerBoardCommentService ownerBoardCommentService;

	/**
	 * 게시글 생성: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param dto 제목, 내용
	 * @param images 사진 file
	 * @return id, 제목, 내용, 생성일
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<CommonResponse<OwnerBoardCreateResponseDto>> createOwnerBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@RequestPart @Valid OwnerBoardCreateRequestDto dto,
		@RequestPart(required = false) List<MultipartFile> images) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardService.saveOwnerBoard(currentUser.getId(), dto, images));
	}

	/**
	 * 게시글 사진 추가: 유저, 관리자 기능
	 * @param currentUser 로그인 유저
	 * @param boardId 사진을 추가할 게시글 ID
	 * @param images 사진 file(여러장 가능)
	 * @return void
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping(value = "/{boardId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<Void>> addImages(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@RequestPart List<MultipartFile> images
	) {
		ownerBoardService.addImages(currentUser.getId(), boardId, images);

		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 게시글 사진 단건 삭제: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 사진을 추가할 게시글 ID
	 * @param imageId 사진 file(단건만 가능)
	 * @return void
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{boardId}/images/{imageId}")
	public ResponseEntity<CommonResponse<Void>> deleteImage(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@PathVariable Long imageId) {
		ownerBoardService.deleteImage(currentUser.getId(), boardId, imageId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 게시글 전체 조회: 전체(유저, 관리자, 비회원) 가능
	 * @param title 제목 검색 가능(nullable)
	 * @param pageable 조회하려는 페이지 위치
	 * @return 전체 게시글 페이징 처리되어 반환
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Page<OwnerBoardGetAllResponseDto>>> getOwnerBoardsAll(
		@RequestParam(required = false) String title,
		@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return CommonResponse.of(SuccessCode.FOUND, ownerBoardService.findAllOwnerBoards(title, pageable));
	}

	/**
	 * 게시글 단건 조회: 전체(유저, 관리자, 비회원) 가능
	 * @param boardId 주인커뮤니티 게시글 ID
	 * @return 게시글 id,제목,내용,작성일,수정일, 댓글리스트(오래된순 10개), 사진리스트
	 */
	@GetMapping("/{boardId}")
	public ResponseEntity<CommonResponse<OwnerBoardGetResponseDto>> getOwnerBoard(
		@PathVariable Long boardId) {

		return CommonResponse.of(SuccessCode.FOUND, ownerBoardService.findOwnerBoard(boardId));
	}

	/**
	 * 게시글 수정: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 수정할 주인커뮤니티 게시글 ID
	 * @param dto 제목, 내용
	 * @return 수정된 OwnerBoard 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/{boardId}")
	public ResponseEntity<CommonResponse<OwnerBoardUpdateResponseDto>> updateOwnerBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardUpdateRequestDto dto) {

		return CommonResponse.of(SuccessCode.UPDATED, ownerBoardService.updateOwnerBoard(currentUser.getId(), boardId, dto));
	}

	/**
	 * 게시글 삭제: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 삭제할 게시글 ID
	 * @return NO_CONTENT 성공코드 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{boardId}")
	public ResponseEntity<CommonResponse<Void>> deleteOwnerBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId) {
		ownerBoardService.deleteOwnerBoard(currentUser.getId(), boardId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 게시글 복구: 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 복구할 게시글 ID
	 * @return RESTORE 성공코드 반환
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{boardId}/restore")
	public ResponseEntity<CommonResponse<Void>> restoreOwnerBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId) {
		ownerBoardService.restoreOwnerBoard(currentUser.getId(), boardId);

		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 주인커뮤니티 댓글 생성: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 게시글 Id
	 * @param dto 내용 작성
	 * @return 댓글 Id, 내용, 생성일, 작성자 ID 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<OwnerBoardCommentCreateResponseDto>> createOwnerBoardComment(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardCommentCreateRequestDto dto) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardCommentService.saveOwnerBoardComment(currentUser.getId(), boardId, dto));
	}

	/**
	 * 게시글의 댓글 전체 조회(페이징): 전체(유저, 관리자, 비회원) 가능
	 * @param boardId 게시글 ID
	 * @param pageable 페이징 설정
	 * @return Page size 10, 생성일 기준 오름차순 정렬
	 */
	@GetMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<Page<OwnerBoardCommentGetResponseDto>>> getOwnerBoardCommentsAll(
		@PathVariable long boardId,
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

		return CommonResponse.of(SuccessCode.FOUND,
			ownerBoardCommentService.findAllOwnerBoardComments(boardId, pageable));
	}

	/**
	 * 주인커뮤니티 댓글 수정: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 댓글이 속한 게시글 ID
	 * @param commentId 댓글 ID
	 * @param dto 수정 내용
	 * @return 댓글 ID, 수정된 내용, 수정일, 작성자 ID
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/{boardId}/comments/{commentId}")
	public ResponseEntity<CommonResponse<OwnerBoardCommentUpdateResponseDto>> updateOwnerBoardComment(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@PathVariable Long commentId,
		@Valid @RequestBody OwnerBoardCommentUpdateRequestDto dto
	) {

		return CommonResponse.of(SuccessCode.UPDATED,
			ownerBoardCommentService.updateOwnerBoardComment(currentUser.getId(), boardId, commentId, dto));
	}

	/**
	 * 주인커뮤니티 댓글 삭제: 유저, 관리자 가능
	 * @param currentUser 로그인 유저
	 * @param boardId 댓글이 속한 게시글 ID
	 * @param commentId 댓글 ID
	 * @return NO_CONTENT 성공코드 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{boardId}/comments/{commentId}")
	public ResponseEntity<CommonResponse<Void>> deleteOwnerBoardComment(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long boardId,
		@PathVariable Long commentId
	) {
		ownerBoardCommentService.deleteOwnerBoardComment(currentUser.getId(), boardId, commentId);

		return CommonResponse.of(SuccessCode.DELETED);
	}
}
