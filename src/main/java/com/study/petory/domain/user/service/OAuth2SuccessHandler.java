package com.study.petory.domain.user.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final AuthServiceImpl authServiceImpl;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// OAuth2 로그인 성공 후 DefaultOAuth2User 에서 사용자 속성 추출
		DefaultOAuth2User oauthUser = (DefaultOAuth2User)authentication.getPrincipal();
		Map<String, Object> attributes = oauthUser.getAttributes();

		// 이메일과 닉네임 추출 (구글 기준)
		String email = (String)attributes.get("email");
		String name = (String)attributes.get("name");

		User user = User.builder()
			.email(email)
			.nickname(name)
			.build();

		// TODO - 배포 전 refreshToken → HttpOnly 헤더 또는 response body 로만 처리 필요
		// 토큰 발급 및 저장 처리
		TokenResponseDto tokens = authServiceImpl.issueToken(user);

		// RefreshToken 헤더에 담기 (인코딩 포함)
		String encodedRefreshToken = URLEncoder.encode(tokens.getRefreshToken(), StandardCharsets.UTF_8);
		response.setHeader("Authorization-Refresh", encodedRefreshToken);

		/* TODO - 배포된 프론트엔트 주소로 변경 필요 (예: "https://www.petory.com/oauth/success")
		 * UriComponentsBuilder 사용
		 * 클라이언트 리다이렉트 (프론트에서 토큰 받을 수 있도록 쿼리 파라미터 전달)
		 */
		String targetUrl = UriComponentsBuilder
			.fromUriString("http://localhost:8080/login-success.html")
			.queryParam("accessToken", tokens.getAccessToken())
			.queryParam("refreshToken", tokens.getRefreshToken())
			.build()
			.toUriString();

		response.sendRedirect(targetUrl);

	}
}
