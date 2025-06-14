package com.study.petory.domain.ownerBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;

public interface OwnerBoardCommentService {
	OwnerBoardComment findOwnerBoardCommentById(Long commentId);

	OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long userId, Long boardId, OwnerBoardCommentCreateRequestDto dto);

	Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(Long boardId, Pageable pageable);

	OwnerBoardCommentUpdateResponseDto updateOwnerBoardComment(Long userId, Long boardId, Long commentId,
		OwnerBoardCommentUpdateRequestDto dto);

	void deleteOwnerBoardComment(Long userId, Long boardId, Long commentId);
}
