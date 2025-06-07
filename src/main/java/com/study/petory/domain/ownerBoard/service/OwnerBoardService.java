package com.study.petory.domain.ownerBoard.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import jakarta.validation.Valid;

public interface OwnerBoardService {
	OwnerBoard findOwnerBoardById(Long boardId);

	OwnerBoardCreateResponseDto saveOwnerBoard(OwnerBoardCreateRequestDto dto, List<MultipartFile> images);

	Page<OwnerBoardGetAllResponseDto> findAllOwnerBoards(String title, int page);

	OwnerBoardGetResponseDto findOwnerBoard(Long boardId);

	OwnerBoardUpdateResponseDto updateOwnerBoard(Long boardId, OwnerBoardUpdateRequestDto dto);

	void deleteOwnerBoard(Long boardId);

	void restoreOwnerBoard(Long boardId);

	void deleteImage(Long boardId, Long imageId);
}
