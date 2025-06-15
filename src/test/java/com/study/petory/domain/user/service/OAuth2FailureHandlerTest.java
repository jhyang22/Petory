package com.study.petory.domain.user.service;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OAuth2FailureHandlerTest {

	@InjectMocks
	private OAuth2FailureHandler failureHandler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AuthenticationException exception;

	@Mock
	private PrintWriter printWriter;

	@Test
	void onAuthentication_실패시_401에러() throws Exception {
		// given
		when(response.getWriter()).thenReturn(printWriter);

		// when
		failureHandler.onAuthenticationFailure(request, response, exception);

		// then
		// HTTP 상태 코드 401 이 설정되었는지 확인
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// 응답 타입이 JSON + UTF-8 인지 확인
		verify(response).setContentType("application/json;charset=UTF-8");

		// 실패 메시지가 올바르게 작성되었는지 확인
		verify(printWriter).write("{\"status\":401,\"message\":\"소셜 로그인에 실패했습니다. 다시 시도해주세요.\"}");
	}
}
