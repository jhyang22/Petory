package com.study.petory.domain.ownerBoard.entity;

import com.study.petory.common.entity.BaseEntityWithCreatedAt;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_owner_board_image")
public class OwnerBoardImage extends BaseEntityWithCreatedAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_board_id")
	private OwnerBoard ownerBoard;

	public OwnerBoardImage(String url, OwnerBoard ownerBoard) {
		this.url = url;
		this.ownerBoard = ownerBoard;
	}
}
