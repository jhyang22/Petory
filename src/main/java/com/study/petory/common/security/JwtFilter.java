package com.study.petory.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.study.petory.common.config.SecurityWhitelist;
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
 * OncePerRequestFilter 를 상속받아 HTTP 요청당 한 번만 실행
 * JWT 인증 필터 - HTTP 요청마다 실행
 * 인증 필요 없는 경로는 WHITELIST 로 필터링하고, 나머지는 JWT 검증 처리
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> loginRefreshToken;

	public JwtFilter(
		JwtProvider jwtProvider,
		@Qualifier("loginRefreshToken")
		RedisTemplate<String, String> loginRefreshToken
	) {
		this.jwtProvider = jwtProvider;
		this.loginRefreshToken = loginRefreshToken;
	}

	// WHITELIST (인증이 필요 없는 경로 리스트 - SecurityWhitelist 와 통일)
	private static final List<String> WHITELIST = SecurityWhitelist.URL_WHITELIST;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 요청 URI
		String url = request.getRequestURI();
		String method = request.getMethod();
		String servletPath = request.getServletPath();
		String contextPath = request.getContextPath();
		String fullUrl = request.getRequestURL().toString();

		debugLog("요청 로그 확인 --------------------------");
		debugLog("Method: " + method);
		debugLog("Request URI: " + url);
		debugLog("Servlet Path: " + servletPath);
		debugLog("Context Path: " + contextPath);
		debugLog("전체 URL: " + fullUrl);

		// GET /owner-boards 하위 경로 모두 허용
		if ("GET".equalsIgnoreCase(method) && url.startsWith("/owner-boards")) {
			debugLog("GET /owner-boards 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// GET /owner-boards 하위 경로 모두 허용
		if ("GET".equalsIgnoreCase(method) && url.startsWith("/places")) {
			debugLog("GET /owner-boards 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 정적 리소스 또는 화이트리스트 우회
		if (url.endsWith(".*(\\.html|\\.css|\\.js|\\.png|\\.jpg|\\.ico)$") || WHITELIST.contains(url)) {
			debugLog("WHITELIST 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 1. Authorization 헤더 확인
		String bearerJwt = request.getHeader("Authorization");

		// 2. 쿼리 파라미터 accessToken 도 허용 (테스트 등)
		if (bearerJwt == null) {
			String queryToken = request.getParameter("accessToken");
			if (queryToken != null) { // <-- NPE 방지! (수정포인트)
				if (queryToken.startsWith("Bearer ")) {
					bearerJwt = queryToken;
				} else {
					bearerJwt = "Bearer " + queryToken;
				}
				debugLog("accessToken 쿼리 파라미터 사용: " + bearerJwt);
			}
		}

		// 3. 최종적으로 bearerJwt 가 null 이면 인증 실패
		if (bearerJwt == null) {
			debugLog("Authorization 헤더 없음. 필터 중단");
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization 헤더가 존재하지 않습니다.");
			return;
		}

		// 4. Bearer 형식 확인
		if (!bearerJwt.startsWith("Bearer ")) {
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효한 Bearer 토큰이 없습니다.");
			return;
		}

		// 5. Bearer 제거 및 토큰 추출, 유효성 검사
		String rawToken;
		try {
			rawToken = jwtProvider.subStringToken(bearerJwt);
			debugLog("추출된 JWT: " + rawToken);
		} catch (CustomException e) {
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
			return;
		}

		// 6. 블랙리스트(로그아웃) 토큰 체크
		if (loginRefreshToken.hasKey("BLACKLIST_" + rawToken)) {
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다.");
			return;
		}

		// 7. 토큰 파싱 및 인증정보 등록
		try {
			Claims claims = jwtProvider.parseRawToken(rawToken);
			Long userId = Long.valueOf(claims.getSubject());
			String email = claims.get("email", String.class);
			String nickname = claims.get("nickname", String.class);

			List<String> roleList = jwtProvider.getRolesFromToken(rawToken);
			List<SimpleGrantedAuthority> authorities = roleList.stream()
				.map(SimpleGrantedAuthority::new)
				.toList();


			debugLog("JWT Claims 파싱 성공 - userId: " + userId);

			CustomPrincipal principal = new CustomPrincipal(userId, email, nickname, authorities);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			debugLog("SecurityContext 등록 완료 - principal: " + principal.getEmail());

			debugLog("JWT 인증 완료. 다음 필터로 전달");
			filterChain.doFilter(request, response);

		} catch (CustomException e) {
			debugLog("JWT 검증 실패 - 이유: " + e.getMessage());
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
		} catch (Exception e) {
			debugLog("검증 실패 - 이유: " + e.getMessage());
			writeErrorResponse(response, ErrorCode.FAILED_AUTHORIZATION.getStatus(), e.getMessage());
		}
	}

	// 오류 응답 반환 (JSON)
	private void writeErrorResponse(HttpServletResponse response, HttpStatusCode status, String message)
		throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", status.value(), message));
	}

	// 디버그 로그
	private void debugLog(String message) {
		log.debug("[JwtFilter] {}", message);
	}
}