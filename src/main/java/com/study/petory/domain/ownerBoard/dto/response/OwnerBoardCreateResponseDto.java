package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCreateResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final List<String> imageUrls;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;

	public static OwnerBoardCreateResponseDto of(OwnerBoard ownerBoard, List<String> imageUrls) {
		return new OwnerBoardCreateResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			imageUrls,
			ownerBoard.getCreatedAt()
		);
	}

}

