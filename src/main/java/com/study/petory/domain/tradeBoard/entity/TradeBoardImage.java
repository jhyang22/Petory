package com.study.petory.domain.tradeBoard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.petory.common.entity.CreationBasedEntity;

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
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_trade_board_image")
public class TradeBoardImage extends CreationBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trade_board_id")
	@JsonIgnore
	private TradeBoard tradeBoard;

	public TradeBoardImage(String url, TradeBoard tradeBoard) {
		this.url = url;
		this.tradeBoard = tradeBoard;
	}
}
