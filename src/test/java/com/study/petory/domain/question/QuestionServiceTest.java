package com.study.petory.domain.question;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;
import com.study.petory.domain.dailyQna.repository.QuestionRepository;
import com.study.petory.domain.dailyQna.service.QuestionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

	@InjectMocks
	private QuestionServiceImpl questionService;

	@Mock
	private QuestionRepository questionRepository;

	private Page<Question> setQuestion(int total, QuestionStatus questionStatus, Pageable pageable) {

		List<Question> questionList = new ArrayList<>();
		LocalDate date = LocalDate.of(2024, 1, 1);
		for (int i = 1; i <= total; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			Question question = new Question(
				"질문 " + i,
				monthDay,
				questionStatus
			);
			date = date.plusDays(1);
			if (questionStatus == QuestionStatus.DELETED) {
				question.deactivateEntity();
			}
			questionList.add(question);
		}

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), questionList.size());
		List<Question> pageC = questionList.subList(start, end);
		Page<Question> testPage = new PageImpl<>(pageC, pageable, questionList.size());
		return testPage;
	}

	@Test
	@DisplayName("관리자가 질문을 저장한다.")
	public void saveQuestion() {
		// given
		QuestionCreateRequestDto request = new QuestionCreateRequestDto("질문입니다.", "01-01");

		given(questionRepository.existsByDate("01-01")).willReturn(false);

		// when
		questionService.saveQuestion(request);

		// then
		verify(questionRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("관리자가 전체 질문을 조회한다.")
	public void findAllQuestion() {
		// given
		Pageable pageable = PageRequest.of(7, 50, Sort.by("date").ascending());
		given(questionRepository.findQuestionByPage(pageable)).willReturn(
			setQuestion(366, QuestionStatus.ACTIVE, pageable));

		// when
		Page<QuestionGetAllResponseDto> responsePage = questionService.findAllQuestion(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(16);
		assertThat(responsePage.getTotalElements()).isEqualTo(366);
		assertThat(responsePage.getTotalPages()).isEqualTo(8);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 단건 질문을 조회한다.")
	public void findOneQuestion() {
		// given
		Long questionId = 1L;

		Question question = new Question("질문 1", "01-01", QuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(question, "id", 1L);
		question.deactivateEntity();

		given(questionRepository.findQuestionByActiveOrInactive(questionId)).willReturn(Optional.of(question));

		// when
		QuestionGetOneResponseDto response = questionService.findOneQuestion(questionId);

		// then
		assertThat(response.getQuestion()).isEqualTo("질문 1");
		assertThat(response.getDate()).isEqualTo("01-01");
		assertThat(response.getQuestionStatus()).isNull();
		assertThat(response.getUpdatedAt()).isNull();
	}

	@Test
	@DisplayName("오늘의 질문을 조회한다.")
	public void findTodayQuestion() {
		// given
		LocalDate date = LocalDate.now();
		String today = date.format(DateTimeFormatter.ofPattern("MM-dd"));

		Question question = new Question("오늘의 질문", today, QuestionStatus.ACTIVE);

		given(questionRepository.findTodayQuestion(today)).willReturn(Optional.of(question));

		// when
		QuestionGetTodayResponseDto response = questionService.findTodayQuestion();

		// then
		assertThat(response.getQuestion()).isEqualTo("오늘의 질문");
		assertThat(response.getDate()).isEqualTo(today);
	}

	@Test
	@DisplayName("관리자가 질문을 수정한다.")
	public void updateQuestion() {
		// given
		Long questionId = 1L;

		Question question = new Question("수정 전 질문", "01-01", QuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(question, "id", 1L);

		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

		QuestionUpdateRequestDto request = new QuestionUpdateRequestDto("수정된 질문", "01-02");

		// when
		questionService.updateQuestion(questionId, request);

		//then
		Question updatedQuestion = questionService.findQuestionByQuestionId(questionId);

		assertThat(updatedQuestion.getQuestion()).isEqualTo("수정된 질문");
		assertThat(updatedQuestion.getDate()).isEqualTo("01-02");
	}

	@Test
	@DisplayName("관리자가 질문을 비활성화 한다.")
	public void InactiveQuestion() {
		// given
		Long questionId = 1L;

		Question question = new Question("비활성화 전 질문", "01-01", QuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(question, "id", 1L);

		given(questionRepository.findQuestionByActive(questionId)).willReturn(Optional.of(question));
		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

		// when
		questionService.inactiveQuestion(questionId);

		// then
		Question inactiveQuestion = questionService.findQuestionByQuestionId(questionId);

		assertThat(inactiveQuestion.getQuestionStatus()).isEqualTo(QuestionStatus.INACTIVE);
	}

	@Test
	@DisplayName("관리자가 비활성화 된 질문을 조회한다.")
	public void findInactiveQuestion() {
		// given
		Pageable pageable = PageRequest.of(1, 50, Sort.by("updatedAt").ascending());

		given(questionRepository.findQuestionByInactive(pageable)).willReturn(
			setQuestion(60, QuestionStatus.INACTIVE, pageable));

		// when
		Page<QuestionGetInactiveResponseDto> responsePage = questionService.findInactiveQuestion(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 비활성화 된 질문을 복구한다.")
	public void updateQuestionStatusActive() {
		// given
		Long questionId = 1L;

		Question question = new Question("질문", "01-01", QuestionStatus.INACTIVE);
		ReflectionTestUtils.setField(question, "id", 1L);

		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

		// when
		questionService.updateQuestionStatusActive(questionId);

		// then
		Question updatedStatusQuestion = questionService.findQuestionByQuestionId(questionId);

		assertThat(updatedStatusQuestion.getQuestionStatus()).isEqualTo(QuestionStatus.ACTIVE);
	}

	@Test
	@DisplayName("관리자가 질문을 삭제한다.")
	public void deactivateQuestion() {
		// given
		Long questionId = 1L;

		Question question = new Question("질문", "01-01", QuestionStatus.ACTIVE);
		ReflectionTestUtils.setField(question, "id", 1L);
		ReflectionTestUtils.setField(question, "deletedAt", null);

		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

		// when
		questionService.deactivateQuestion(questionId);

		// then
		Question deactivatedQuestion = questionService.findQuestionByQuestionId(questionId);

		assertThat(deactivatedQuestion.getQuestionStatus()).isEqualTo(QuestionStatus.DELETED);
		assertThat(deactivatedQuestion.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("관리자가 삭제된 질문을 조회한다.")
	public void findQuestionByDeleted() {
		// given
		Pageable pageable = PageRequest.of(1, 50, Sort.by("deletedAt").ascending());

		given(questionRepository.findQuestionByDeleted(pageable)).willReturn(
			setQuestion(60, QuestionStatus.DELETED, pageable));

		// when
		Page<QuestionGetDeletedResponse> responsePage = questionService.findQuestionByDeleted(pageable);

		// then
		assertThat(responsePage.getContent()).hasSize(10);
		assertThat(responsePage.getTotalElements()).isEqualTo(60);
		assertThat(responsePage.getTotalPages()).isEqualTo(2);
		assertThat(responsePage.isLast()).isTrue();
	}

	@Test
	@DisplayName("관리자가 삭제된 질문을 복구한다.")
	public void restoreQuestion() {
		// given
		Long questionId = 1L;
		LocalDateTime deletedTime = LocalDateTime.now();

		Question question = new Question("질문", "01-01", QuestionStatus.DELETED);
		ReflectionTestUtils.setField(question, "id", 1L);
		ReflectionTestUtils.setField(question, "deletedAt", deletedTime);

		given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

		// when
		questionService.restoreQuestion(questionId);

		// then
		Question restoredQuestion = questionService.findQuestionByQuestionId(questionId);

		assertThat(restoredQuestion.getQuestionStatus()).isEqualTo(QuestionStatus.ACTIVE);
		assertThat(restoredQuestion.getDeletedAt()).isNull();
	}
}
