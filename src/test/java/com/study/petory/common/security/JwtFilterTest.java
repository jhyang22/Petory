package com.study.petory.common.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.study.petory.common.config.SecurityWhitelist;
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;

/*
 * JwtFilter 의 인증 로직을 단위 테스트로 검증
 * Spring Security Context 없이 필터 단독으로 테스트
 */
@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

	@InjectMocks
	private JwtFilter jwtFilter;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private RedisTemplate<String, String> loginRefreshToken;

	@Mock
	private SecurityWhitelist securityWhitelist;

	@Mock
	private FilterChain filterChain;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	void setUp() {

		// 매 테스트마다 새로운 요청 객체 생성
		request = new MockHttpServletRequest();

		// 매 테스트마다 새로운 응답 객체 생성
		response = new MockHttpServletResponse();

		// 이전 인증 정보 제거
		SecurityContextHolder.clearContext();
	}

	@Test
	void 정상_토큰_인증_성공() throws Exception {


		/* [given]
		 * 1. 정상 토큰
		 * 2. Bearer 제거한 토큰
		 * 3. 보호된 URI 설정
		 * 4. Authorization 헤더 추가
		 */
		String token = "Bearer valid.token.here";
		String rawToken = "valid.token.here";
		request.setRequestURI("/api/protected");
		request.addHeader("Authorization", token);

		/*
		 * 1. 클레임 모킹
		 * 2. 유저 ID 반환
		 * 3. 이메일 반환
		 * 4. 닉네임 반환
		 */
		Claims claims = mock(Claims.class);
		when(claims.getSubject()).thenReturn("1");
		when(claims.get("email", String.class)).thenReturn("test@email.com");
		when(claims.get("nickname", String.class)).thenReturn("tester");

		/*
		 * 1. Bearer 제거
		 * 2. 블랙리스트 아님
		 * 3. 정상 파싱 결과 반환
		 * 4. 역할 리스트 반환
		 */
		when(jwtProvider.subStringToken(token)).thenReturn(rawToken);
		when(loginRefreshToken.hasKey("BLACKLIST_" + rawToken)).thenReturn(false);
		when(jwtProvider.parseRawToken(rawToken)).thenReturn(claims);
		when(jwtProvider.getRolesFromToken(rawToken)).thenReturn(List.of("ROLE_USER"));

		/* [when]
		 * 필터 직접 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 다음 필터 호출되었는지 확인
		 * // 인증 정보가 등록되었는지 확인
		 */
		verify(filterChain, times(1)).doFilter(request, response);
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void Authorization_헤더_없으면_401반환() throws Exception {

		/* [given]
		 * 보호된 URL 설정
		 */
		request.setRequestURI("/api/protected");

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 401 반환 여부 확인
		 * 필터 체인 호출되지 않았는지 확인
		 */
		assertEquals(401, response.getStatus());
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void 토큰_형식_오류_401반환() throws Exception {

		/* [given]
		 * 보호된 URL
		 * Bearer 없이 잘못된 포맷
		 */
		request.setRequestURI("/api/protected");
		request.addHeader("Authorization", "InvalidFormat");

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 응답이 401인지 확인
		 * 필터 체인 호출되지 않음
		 */
		assertEquals(401, response.getStatus());
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void 블랙리스트_토큰이면_401반환() throws Exception {

		// [given]
		String token = "Bearer black.token";
		String rawToken = "black.token";
		request.setRequestURI("/api/protected");
		request.addHeader("Authorization", token);

		/*
		 * 1. Bearer 제거
		 * 2. 블랙리스트 처리
		 */
		when(jwtProvider.subStringToken(token)).thenReturn(rawToken);
		when(loginRefreshToken.hasKey("BLACKLIST_" + rawToken)).thenReturn(true);

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 401 확인
		 * 다음 필터 호출 안 됨
		 */
		assertEquals(401, response.getStatus());
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void 토큰_만료_예외_401반환() throws Exception {

		// [given]
		String token = "Bearer expired.token";
		String rawToken = "expired.token";
		request.setRequestURI("/api/protected");
		request.addHeader("Authorization", token);

		/*
		 * 1. Bearer 제거
		 * 2. 블랙리스트 아님
		 * 3. 만료 예외 발생
		 */
		when(jwtProvider.subStringToken(token)).thenReturn(rawToken);
		when(loginRefreshToken.hasKey("BLACKLIST_" + rawToken)).thenReturn(false);
		when(jwtProvider.parseRawToken(rawToken)).thenThrow(new CustomException(ErrorCode.EXPIRED_TOKEN));

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 401 반환 확인
		 * 필터 체인 호출 안 됨
		 */
		assertEquals(401, response.getStatus());
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void 화이트리스트_URL_우회() throws Exception {

		/* [given]
		 * 화이트리스트 URL
		 * 화이트리스트에 등록됨
		 */
		request.setRequestURI("/auth/reissue");
		when(securityWhitelist.getUrlWhitelist()).thenReturn(List.of("/auth/reissue"));

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 필터 통과 확인
		 */
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	void 허용된_GET_경로_우회() throws Exception {

		/* [given]
		 * GET 요청
		 * 허용된 Prefix 경로
		 */
		request.setMethod("GET");
		request.setRequestURI("/places/abc");

		/* [when]
		 * 필터 호출
		 */
		jwtFilter.doFilterInternal(request, response, filterChain);

		/* [then]
		 * 필터 통과 확인
		 */
		verify(filterChain, times(1)).doFilter(request, response);
	}
}
