package com.study.petory.domain.tradeBoard.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_trade_board")
@NoArgsConstructor
@Where(clause = "status != 'DELETED'")
public class TradeBoard extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private TradeCategory category;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@OneToMany(mappedBy = "tradeBoard", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true)
	private List<TradeBoardImage> images = new ArrayList<>();

	@Column(nullable = false)
	private Integer price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TradeBoardStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public TradeBoard(TradeCategory category, String title, String content, Integer price, User user) {
		this.category = category;
		this.title = title;
		this.content = content;
		this.price = price;
		this.status = TradeBoardStatus.FOR_SALE;
		this.user = user;
	}

	public void updateTradeBoard(TradeBoardUpdateRequestDto requestDto) {
		this.category = requestDto.getCategory();
		this.title = requestDto.getTitle();
		this.content = requestDto.getContent();
		this.price = requestDto.getPrice();
		this.status = requestDto.getStatus();
	}

	public void updateStatus(TradeBoardStatus status) {
		this.status = status;
	}

	// user 검증 메서드
	public boolean isOwner(Long userId) {
		return this.getUser().getId().equals(userId);
	}

	public void addImage(TradeBoardImage image) {
		images.add(image);
		image.setTradeBoard(this);
	}

	public Long getUserId() {
		return this.getUser().getId();
	}
}
