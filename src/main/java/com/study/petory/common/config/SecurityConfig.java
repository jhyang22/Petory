package com.study.petory.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.study.petory.common.security.CustomAccessDeniedHandler;
import com.study.petory.common.security.JwtAuthenticationEntryPoint;
import com.study.petory.common.security.JwtFilter;
import com.study.petory.domain.user.service.CustomOAuth2UserService;
import com.study.petory.domain.user.service.OAuth2FailureHandler;
import com.study.petory.domain.user.service.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity  // Spring Security 설정 활성화
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize, @PostAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	/*
	 * 1. .csrf : CSRF 설정 → JWT 기반이기 때문에 csrf 보호 비활성화
	 * 2. .sessionManagement : 세션 관리 방식 설정
	 * 3. .authorizeHttpRequests : URL 접근 권한 설정
			.requestMatchers : 해당 경로들은 로그인 없이 접근 허용 → permitAll(누구나 접근 허용)
			.anyRequest().authenticated() : 그 외의 모든 요청은 인증된 사용자만 접근 가능
	 * 4. exceptionHandling : 인증 예외 처리 커스터마이징 → 인증 안 된 사용자가 보호된 리소스 접근 시 예외를 JSON 으로 반환
			.authenticationEntryPoint : 인증 안 된 사용자 접근 시 동작 지정
			response.setStatue : HTTP 상태 코드 401 UNAUTHORIZED 로 응답
			response.setContentType : 응답 형식을 JSON 으로 설정
			response.getWriter().write() : 사용자에게 JSON 메시지를 반환
	 * 5. addFilterBefore : JWT 필터를 Security 필터 체인에 등록 → JWT 토큰 유효한지 먼저 검증 후 인증 처리 진행
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CORS 설정 적용
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Security 전용 WHITELIST
				.requestMatchers(SecurityWhitelist.URL_WHITELIST.toArray(new String[0])).permitAll()
				// GET 메서드의 특정 경로 한정 허용
				.requestMatchers(HttpMethod.GET, SecurityWhitelist.PERMIT_GET_PREFIXES.toArray(new String[0])).permitAll()
				.anyRequest().authenticated()
			)

			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 분리된 클래스 사용
				.accessDeniedHandler(customAccessDeniedHandler)
			)

			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)  // OAuth2 사용자 정보 처리
				)
				.successHandler(oAuth2SuccessHandler)  // 로그인 성공 후 JWT 발급
				.failureHandler(oAuth2FailureHandler)  // 로그인 실패 시 처리
			)

			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/*
	 * 비밀번호 암호화용 Bean
	 * 로그인 시 사용자 입력 비밀번호를 암호화된 비밀번호와 비교할 때 사용된다
	 * Security 는 내부적으로 이 메서드를 사용해 matches() 검사를 수행한다
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	/*
	 * CORS 설정 위한 Bean
	 * React 등 다른 도메인에서 요청 시 Access-Control-Allow-Credentials, Access-Control-Allow-Origin 등을 정상 처리
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		// CORS 설정 객체 생성
		CorsConfiguration config = new CorsConfiguration();

		// 허용할 프론트 주소 (클라이언트 도메인 지정)
		config.setAllowedOrigins(List.of("http://localhost:3000"));  // React 등 프론트엔드 개발 서버

		// 허용할 HTTP 메서드 지정
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		/* TODO - 배포 전 체크
		 * 허용할 요청 헤더 지정 (예: Authorization, Content-Type 등)
		 */
		config.setAllowedHeaders(List.of("*"));

		// 자격 증명 포함 허용 (예: 쿠키, Authorization 헤더 등)
		config.setAllowCredentials(true);  // 쿠키/인증정보 포함 허용 (프론트에서 withCredentials: true 필요)

		// CORS 설정을 특정 경로 패턴에 매핑
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 모든 요청 경로(/**)에 대해 CORS 설정 적용
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}
