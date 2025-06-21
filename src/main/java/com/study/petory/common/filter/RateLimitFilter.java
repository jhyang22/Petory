package com.study.petory.common.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.study.petory.common.security.CustomPrincipal;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

	private final ProxyManager<String> proxyManager;
	private final Supplier<BucketConfiguration> bucketConfigurationSupplier = createBucketConfig();

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		FilterChain filterChain) throws
		IOException,
		ServletException {

		// 신고 요청을 확인
		String uri = httpServletRequest.getRequestURI();

		// \\d+ 는 숫자 1개 이상으로 이루어진 문자열을 의미
		String keyPrefix;
		if (uri.matches("/places/\\d+/reviews")) {
			keyPrefix = "rate-limit:review:";
		} else if (uri.matches("/places/\\d+/reports")) {
			keyPrefix = "rate-limit:report:";
		} else {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}

		// userId를 확인
		Long userId = extractUserId();
		if (userId == null) {
			// sendError의 경우 매개변수로 0번 째 인덱스는 int, 1번 째 인덱스는 String으로 받기 때문에 .value로 값을 꺼내줘야 함
			httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.");
			return;
		}

		// user 마다 고유 키를 설정
		String key = keyPrefix + userId;

		// 버킷 생성
		Bucket bucket = proxyManager.builder().build(key, bucketConfigurationSupplier);

		// 버킷에서 요청시마다 토큰 1개를 소비
		// 만약 잔여 토큰이 0개라면 요청 거부
		if (bucket.tryConsume(1)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			httpServletResponse.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지
			httpServletResponse.setContentType("text/plain; charset=UTF-8");
			httpServletResponse.getWriter().write("같은 요청은 1시간에 최대 10회까지 가능합니다.");
		}
	}

	// final 필드로 부여하여 계속 재사용하는 방식
	private Supplier<BucketConfiguration> createBucketConfig() {

		// 버킷 설정 Greedy 방식
		// 1시간에 최대 10개의 토큰 사용가능
		// 토큰은 1시간 주기로 10개의 토큰을 한번에 채워줌
		return () -> BucketConfiguration.builder()
			.addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofHours(1)))
			.build();
	}

	private Long extractUserId() {

		// 현재 로그인된 사용자의 인증 정보를 꺼내는 과정
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomPrincipal currentUser) {
			return currentUser.getId();
		}

		// 로그인 되지 않았을 경우 null이 반환되고 401 에러 반환
		return null;
	}
}
