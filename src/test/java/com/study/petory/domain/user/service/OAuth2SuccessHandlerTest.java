package com.study.petory.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

	@Mock
	private AuthServiceImpl authServiceImpl;

	@InjectMocks
	private OAuth2SuccessHandler oAuth2SuccessHandler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Test
	void onAuthenticationSuccess_소셜로그인_성공시_토큰발급_및_리다이렉트_처리() throws Exception {

		// given: OAuth2 로그인 사용자 정보 세팅 (email, name)
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", "user@example.com");
		attributes.put("name", "nickname");

		// OAuth2User 객체 생성 (테스트용 사용자)
		OAuth2User oauth2User = new DefaultOAuth2User(
			java.util.List.of(new OAuth2UserAuthority(attributes)),
			attributes,
			"email"
		);

		// Authentication 객체 생성
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());

		// 모의 발급 토큰 응답 설정
		TokenResponseDto tokens = new TokenResponseDto("accessTokenValue", "refreshTokenValue");

		when(authServiceImpl.issueToken(any(User.class))).thenReturn(tokens);
		doNothing().when(response).setHeader(anyString(), anyString());
		doNothing().when(response).sendRedirect(anyString());

		// when: OAuth2 로그인 성공 처리 메서드 실행
		oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

		// then: 다음 동작이 모두 수행되었는지 검증
		verify(authServiceImpl).issueToken(any(User.class)); // 토큰 발급 시도 확인
		verify(response).setHeader(eq("Authorization-Refresh"), anyString()); // 헤더에 RefreshToken 설정 확인
		verify(response).sendRedirect(argThat(url ->
			url.contains("accessToken=accessTokenValue") &&
				url.contains("refreshToken=refreshTokenValue")
		)); // 프론트 리다이렉트 URL 확인
	}
}
