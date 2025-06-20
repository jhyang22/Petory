package com.study.petory.domain.user.entity;

import java.util.List;

import com.study.petory.common.entity.TimeFeatureBasedEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_user")
@NoArgsConstructor
public class User extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	@Column(nullable = false)
	private String email;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_private_info_id")
	private UserPrivateInfo userPrivateInfo;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private List<UserRole> userRole;

	@Builder
	public User(String nickname, String email, UserPrivateInfo userPrivateInfo, List<UserRole> userRole) {
		this.email = email;
		this.nickname = nickname;
		this.userPrivateInfo = userPrivateInfo;
		this.userRole = userRole;
	}

	public void updateNickname(String newNickname) {
		this.nickname = newNickname;
	}

	// userId 검증 메서드
	public boolean isEqualId(Long userId) {
		return this.id.equals(userId);
	}

	public boolean hasRole(Role role) {
		return userRole.stream().anyMatch(userRole -> userRole.getRole().equals(role));
	}
}
