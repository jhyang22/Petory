package com.study.petory.common.config;

import java.util.List;

// TODO - 배포 전 체크
public class SecurityWhitelist {

	// 인증 없이 접근 가능한 경로
	public static final List<String> URL_WHITELIST = List.of(
		"/auth/reissue",
		"/auth/logout",
		"/users/test-login",
		"/login.html",
		"/favicon.ico",
		"/map.html",
		"/trade-boards",
		"/trade-boards/{tradeBoardId}",
		"/questions/today"
	);

	// GET 메서드만 허용되는 경로 (HttpMethod.GET, /places/**) 형태로 허용
	public static final List<String> PERMIT_GET_PREFIXES = List.of(
		"/owner-boards",
		"/places"
	);
}
