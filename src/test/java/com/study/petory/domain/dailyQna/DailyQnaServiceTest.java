package com.study.petory.domain.dailyQna;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.dailyQna.repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetHiddenResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;
import com.study.petory.domain.dailyQna.service.DailyQnaServiceImpl;
import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class DailyQnaServiceTest {

	@InjectMocks
	private DailyQnaServiceImpl dailyQnaService;

	@Mock
	private DailyQnaRepository dailyQnaRepository;

	@Mock
	private UserService userService;

	@Mock
	private QuestionServiceImpl questionService;

	private final UserPrivateInfo testUserInfo = new UserPrivateInfo(
		"1L",
		"실명",
		"000-0000-0000"
	);

	private final List<UserRole> testUserRole = new ArrayList<>(List.of(new UserRole(Role.USER)));

	private final User testUser = new User(
		"닉네임",
		"email@email.com",
		testUserInfo,
		testUserRole
	);

	private final Question testQuestion = new Question(
		"오늘은 뭘 먹었어?",
		"01-01",
		QuestionStatus.ACTIVE
	);

	// 날짜의 정렬을 확인하는 메서드
	private void asserSortedByCreatedAtDesc(List<DailyQnaGetResponseDto> dtoList) {
		List<LocalDateTime> resultCreatedAt = dtoList.stream()
			.map(DailyQnaGetResponseDto::getCreatedAt)
			.collect(Collectors.toList());

		List<LocalDateTime> sortedCreatedAt = new ArrayList<>(resultCreatedAt);
		sortedCreatedAt.sort(Comparator.reverseOrder());

		assertThat(resultCreatedAt).isEqualTo(sortedCreatedAt);
	}

	// 더미 DailyQna 생성 메서드
	private DailyQna setDailyQna(String answer, DailyQnaStatus dailyQnaStatus, String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DailyQna dummyDailyQna = DailyQna.builder()
			.user(testUser)
			.question(testQuestion)
			.answer(answer)
			.dailyQnaStatus(dailyQnaStatus)
			.build();
		ReflectionTestUtils.setField(dummyDailyQna, "createdAt", LocalDateTime.parse(date, formatter));
		if (dailyQnaStatus.equals(QuestionStatus.DELETED)) {
			ReflectionTestUtils.setField(dummyDailyQna, "deletedAt", LocalDateTime.now());
		}
		return dummyDailyQna;
	}

	// 더미 DailyQna 생성 메서드
	private Page<DailyQna> setDailyQnaPage(int total, DailyQnaStatus dailyQnaStatus, Pageable pageable) {
		List<DailyQna> dailyQnaList = new ArrayList<>();
		for (int i = 1; i <= total; i++) {
			DailyQna dailyQna = setDailyQna(
				"답변 " + i,
				dailyQnaStatus,
				"2023-01-01 00:00:00"
			);
			if (dailyQnaStatus == DailyQnaStatus.DELETED) {
				dailyQna.deactivateEntity();
			}
			dailyQnaList.add(dailyQna);
		}
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), dailyQnaList.size());
		List<DailyQna> pageC = dailyQnaList.subList(start, end);
		Page<DailyQna> testPage = new PageImpl<>(pageC, pageable, dailyQnaList.size());
		return testPage;
	}

	@Test
	@DisplayName("사용자가 질문에 대한 답변을 저장한다.")
	public void saveDailyQna() {
		// given
		Long userId = 1L;
		Long questionId = 1L;

		DailyQnaCreateRequestDto requestDto = new DailyQnaCreateRequestDto("답변");

		given(userService.getUserById(userId)).willReturn(testUser);
		given(questionService.findQuestionByQuestionId(questionId)).willReturn(testQuestion);

		// when
		dailyQnaService.saveDailyQna(userId, questionId, requestDto);
		// then
		verify(dailyQnaRepository, times(1)).save(any(DailyQna.class));
	}

	@Test
	@DisplayName("질문에 사용자가 남긴 모든 답변을 조회한다.")
	public void getAllAnswer() {
		// given
		Long userId = 1L;
		Long questionId = 1L;

		DailyQna d1 = setDailyQna("답변 1", DailyQnaStatus.ACTIVE, "2022-01-01 00:00:00");
		DailyQna d2 = setDailyQna("답변 2", DailyQnaStatus.ACTIVE, "2024-01-01 00:00:00");
		DailyQna d3 = setDailyQna("답변 3", DailyQnaStatus.ACTIVE, "2023-01-01 00:00:00");

		List<DailyQna> responseList = new ArrayList<>(Arrays.asList(d1, d3, d2));

		given(dailyQnaRepository.findDailyQna(userId, questionId)).willReturn(responseList);

		// when
		List<DailyQnaGetResponseDto> response = dailyQnaService.findDailyQna(userId, questionId);

		// then
		assertThat(response).hasSize(3);
		assertThat(response.get(0).getAnswer()).isEqualTo("답변 1");
		assertThat(response.get(1).getAnswer()).isEqualTo("답변 3");
		assertThat(response.get(2).getAnswer()).isEqualTo("답변 2");
	}

	@Test
	@DisplayName("사용자가 답변을 수정한다.")
	public void updateDailyQna() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyQna dailyQna = setDailyQna("수정 전 답변", DailyQnaStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyQna, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyQnaRepository.findDailyQnaByActive(dailyQnaId)).willReturn(Optional.of(dailyQna));
		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(dailyQna));

		DailyQnaUpdateRequestDto request = new DailyQnaUpdateRequestDto("수정 후 답변");

		// when
		dailyQnaService.updateDailyQna(userId, dailyQnaId, request);

		// then
		DailyQna updateQna = dailyQnaService.findDailyQnaByDailyQnaId(dailyQnaId);

		assertThat(updateQna.getAnswer()).isEqualTo("수정 후 답변");
	}

	@Test
	@DisplayName("사용자가 답변을 숨김 처리한다.")
	public void hideDailyQna() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyQna dailyQna = setDailyQna("숨기기 전 답변", DailyQnaStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyQna, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyQnaRepository.findDailyQnaByActive(dailyQnaId)).willReturn(Optional.of(dailyQna));
		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(dailyQna));

		// when
		dailyQnaService.hideDailyQna(userId, dailyQnaId);

		//then
		DailyQna hideDailyQna = dailyQnaService.findDailyQnaByDailyQnaId(dailyQnaId);

		assertThat(hideDailyQna.getDailyQnaStatus()).isEqualTo(DailyQnaStatus.HIDDEN);
	}

	@Test
	@DisplayName("사용자가 숨김 처리한 답변을 조회한다.")
	public void findHiddenDailyQna() {
		// given
		Long userId = 1L;

		Pageable pageable = PageRequest.of(1, 50, Sort.by("date").ascending());
		given(dailyQnaRepository.findDailyQnaByHidden(userId, pageable)).willReturn(
			setDailyQnaPage(60, DailyQnaStatus.HIDDEN, pageable));

		// when
		Page<DailyQnaGetHiddenResponse> responsePage = dailyQnaService.findHiddenDailyQna(userId, pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("사용자가 숨김 처리한 답변을 복구한다.")
	public void updateDailyQnaStatusActive() {
		// given
		Long userId = 1L;
		Long dailyQnaId = 1L;

		DailyQna dailyQna = setDailyQna("삭제 전 답변", DailyQnaStatus.HIDDEN, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyQna, "id", 1L);
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(dailyQna));

		// when
		dailyQnaService.updateDailyQnaStatusActive(userId, dailyQnaId);

		// then
		DailyQna updatedDailyQna = dailyQnaService.findDailyQnaByDailyQnaId(dailyQnaId);

		assertThat(updatedDailyQna.getDailyQnaStatus()).isEqualTo(DailyQnaStatus.ACTIVE);
	}

	@Test
	@DisplayName("관리자가 답변을 삭제한다.")
	public void deleteDailyQna() {
		// given
		Long dailyQnaId = 1L;

		DailyQna dailyQna = setDailyQna("삭제 전 답변", DailyQnaStatus.ACTIVE, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(dailyQna, "id", 1L);

		given(dailyQnaRepository.findDailyQnaByActive(dailyQnaId)).willReturn(Optional.of(dailyQna));
		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(dailyQna));

		// when
		dailyQnaService.deleteDailyQna(dailyQnaId);

		// then
		DailyQna deletedDailyQna = dailyQnaService.findDailyQnaByDailyQnaId(dailyQnaId);

		assertThat(deletedDailyQna.getDailyQnaStatus()).isEqualTo(DailyQnaStatus.DELETED);
		assertThat(deletedDailyQna.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("관리자가 삭제된 답변 조회")
	public void findDeletedDailyQna() {
		// given
		Long userId = 1L;

		Pageable pageable = PageRequest.of(1, 50, Sort.by("date").ascending());
		given(dailyQnaRepository.findDailyQnaByDeleted(userId, pageable)).willReturn(
			setDailyQnaPage(60, DailyQnaStatus.DELETED, pageable));

		// when
		Page<DailyQnaGetDeletedResponse> responsePage = dailyQnaService.findDeletedDailyQna(userId, pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 삭제된 답변 복구")
	public void restoreDailyQna() {
		// given
		Long dailyQnaId = 1L;

		DailyQna deletedDailyQna = setDailyQna("삭제 전 답변", DailyQnaStatus.DELETED, "2022-01-01 00:00:00");
		ReflectionTestUtils.setField(deletedDailyQna, "id", 1L);

		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(deletedDailyQna));
		given(dailyQnaRepository.findById(dailyQnaId)).willReturn(Optional.of(deletedDailyQna));

		// when
		dailyQnaService.restoreDailyQna(dailyQnaId);

		// then
		DailyQna restoreDailyQna = dailyQnaService.findDailyQnaByDailyQnaId(dailyQnaId);

		assertThat(restoreDailyQna.getDailyQnaStatus()).isEqualTo(DailyQnaStatus.ACTIVE);
		assertThat(restoreDailyQna.getDeletedAt()).isNull();

	}
}
