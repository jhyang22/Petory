package com.study.petory.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DeletionBasedEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime deletedAt;

	// soft delete 시 사용
	public void deactivateEntity() {
		this.deletedAt = LocalDateTime.now();
	}

	// soft delete 복구 시 사용
	public void restoreEntity() {
		this.deletedAt = null;
	}

	public boolean isDeletedAtNull() {
		return this.deletedAt == null;
	}
}
