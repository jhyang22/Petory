package com.study.petory.common.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/*
 * JwtProvider 의 주요 API 를 검증하는 단위 테스트
 *
 * AccessToken 생성 및 파싱
 * 토큰 만료 예외 처리
 * 서명 검증 예외 처리
 * Redis 기반 RefreshToken 검증
 */
@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

	@InjectMocks
	private JwtProvider jwtProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	/*
	 * 테스트용 시크릿 키(Base64 인코딩) 주입.
	 * 실제 application.properties 의 설정을 대신함
	 */
	@Value("${jwt.secret.key}")
	private String secretKey = Base64.getEncoder()
		.encodeToString("testSecretKeytestSecretKeytestSecretKey".getBytes());

	/*
	 * 각 테스트 전, 리플렉션으로 secretKey 필드를 설정하고 init()을 호출해 Key 객체를 준비
	 * 리플렉션(reflection): 자바에서 런타임에 클래스의 정보(필드, 메서드, 생성자 등)를 조회하거나 조작할 수 있도록 해 주는 기능
	 */
	@BeforeEach
	void setUp() {

		// private 필드 secretKey 에 값 주입
		ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
		// @PostConstruct 역할 수행
		jwtProvider.init();
	}

	@Test
	void AccessToken_생성_및_Claim_추출_success() {

		/* [given]
		 * 유저 정보
		 */
		long userId = 1L;
		String email = "user@example.com";
		String nickname = "nickname";
		List<String> roles = List.of("ROLE_USER");

		/* [when]
		 * 토큰 생성
		 * 토큰 파싱
		 */
		String token = jwtProvider.createAccessToken(userId, email, nickname, roles);
		Claims claims = jwtProvider.getClaims(token);

		/* [then]
		 * Claim 정보 검증
		 */
		assertEquals("1", claims.getSubject(), "Subject에 userId가 문자열로 저장되어야 한다");
		assertEquals(email, claims.get("email", String.class), "Custom Claim 'email' 검증");
		assertEquals(nickname, claims.get("nickname", String.class), "Custom Claim 'nickname' 검증");
	}

	@Test
	void 만료된_JWT_파싱하면_throwsException() {

		/* [given]
		 * 만료된 토큰 생성
		 */
		Key key = (Key) ReflectionTestUtils.getField(jwtProvider, "key");
		String expiredToken = Jwts.builder()
			.setSubject("1")
			.setExpiration(new Date(System.currentTimeMillis() - 1_000)) // 이미 만료
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
		String bearer = "Bearer " + expiredToken;

		/* [when] / [then]
		 * 만료 예외 발생
		 */
		CustomException ex = assertThrows(CustomException.class, () ->
			jwtProvider.getClaims(bearer));
		assertEquals(ErrorCode.EXPIRED_TOKEN, ex.getErrorCode(),
			"만료된 토큰은 EXPIRED_TOKEN 예외를 던져야 한다");
	}

	@Test
	void 위조된_JWT_파싱하면_throwsException() {

		/* [given]
		 * 잘못된 키로 서명된 토큰 생성
		 */
		String tamperedKey = secretKey + "x";
		Key badKey = Keys.hmacShaKeyFor(tamperedKey.getBytes());
		String badToken = Jwts.builder()
			.setSubject("1")
			.signWith(badKey, SignatureAlgorithm.HS256)
			.compact();
		String bearerBad = "Bearer " + badToken;

		/* [when] / [then]
		 * 서명 검증 실패 예외 발생
		 */
		CustomException ex = assertThrows(CustomException.class, () ->
			jwtProvider.getClaims(bearerBad)
		);
		assertEquals(ErrorCode.WRONG_SIGNATURE, ex.getErrorCode(),
			"서명 검증 실패 시 WRONG_SIGNATURE 예외가 발생해야 한다");
	}

	@Test
	void Redis에_저장된_RefreshToken과_일치하면_true_반환() {

		/* [given]
		 * Redis 에 저장된 토큰과 동일한 토큰 요청
		 */
		Long userId = 1L;
		String refreshToken = "Bearer mockToken";

		// RedisTemplate 의 opsForValue() 모킹
		ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(ops);
		when(ops.get(String.valueOf(userId))).thenReturn(refreshToken);

		/* [when]
		 * 검증 요청
		 */
		boolean result = jwtProvider.isValidRefreshToken(userId, refreshToken);

		/* [then]
		 * true 반환 확인
		 */
		assertTrue(result, "Redis에 일치하는 리프레시 토큰이 있으면 true 반환해야 한다");
	}
}

