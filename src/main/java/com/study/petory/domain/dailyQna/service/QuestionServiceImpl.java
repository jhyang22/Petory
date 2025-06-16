package com.study.petory.domain.dailyQna.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.DateUtil;
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
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;
	private final UserService userService;

	/*
	테스트 코드 전용
	todo 삭제 예정
	 */
	@Override
	public void setQuestion() {
		LocalDate date = LocalDate.of(2024, 1, 1);
		for (int i = 1; i <= 366; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			Question question = new Question(
				"질문 " + i,
				monthDay,
				QuestionStatus.ACTIVE
			);
			date = date.plusDays(1);
			questionRepository.save(question);
		}
	}

	// id로 질문을 조회
	@Override
	public Question findQuestionByQuestionId(Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	// id로 Active 상태인 질문 조회
	@Override
	public Question findQuestionByActive(Long questionId) {
		return questionRepository.findQuestionByActive(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	// id로 Active 또는 Inactive 상태인 질문 조회
	@Override
	public Question findQuestionByActiveOrInactive(Long questionId) {
		return questionRepository.findQuestionByActiveOrInactive(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	// 해당 일자에 해당하는 질문이 있는지 확인
	@Override
	public void existsByDate(String date) {
		if (questionRepository.existsByDate(date)) {
			throw new CustomException(ErrorCode.DATE_IS_EXIST);
		}
	}

	// 질문을 저장 admin
	@Override
	@Transactional
	public void saveQuestion(QuestionCreateRequestDto request) {
		existsByDate(request.getDate());

		questionRepository.save(Question.builder()
			.question(request.getQuestion())
			.date(request.getDate())
			.questionStatus(QuestionStatus.ACTIVE)
			.build()
		);
	}

	// 질문 전체 조회 admin
	@Override
	public Page<QuestionGetAllResponseDto> findAllQuestion(Pageable pageable) {
		Page<Question> questionPage = questionRepository.findQuestionByPage(pageable);
		return questionPage.map(QuestionGetAllResponseDto::from);
	}

	// 질문 단건 조회 admin
	@Override
	public QuestionGetOneResponseDto findOneQuestion(Long questionId) {
		return QuestionGetOneResponseDto.from(findQuestionByActiveOrInactive(questionId));
	}

	// 오늘의 질문 조회
	@Override
	@Cacheable(value = "todayQuestion")
	public QuestionGetTodayResponseDto findTodayQuestion() {
		String today = DateUtil.getFormatDate();
		Question question = questionRepository.findTodayQuestion(today)
			.orElseThrow(() -> new CustomException(ErrorCode.TODAY_QUESTION_IS_DEACTIVATED));
		return QuestionGetTodayResponseDto.from(question);
	}

	// 질문 수정 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void updateQuestion(Long questionId, QuestionUpdateRequestDto request) {
		Question question = findQuestionByQuestionId(questionId);
		question.update(request.getQuestion(), request.getDate());
	}

	// 질문 비활성화 admin
	@Override
	@Transactional
	@CacheEvict(value = "todayQuestion", allEntries = true)
	public void inactiveQuestion(Long questionId) {
		Question question = findQuestionByActive(questionId);
		question.updateStatusInactive();
	}

	// 비활성화 된 질문 조회 admin
	@Override
	public Page<QuestionGetInactiveResponseDto> findInactiveQuestion(Pageable pageable) {
		Page<Question> questionPage = questionRepository.findQuestionByInactive(pageable);
		return questionPage
			.map(QuestionGetInactiveResponseDto::from);
	}

	// 비활성화 된 질문 복구 admin
	@Override
	@Transactional
	public void updateQuestionStatusActive(Long questionId) {
		Question question = findQuestionByQuestionId(questionId);
		question.updateStatusActive();
	}

	// 질문 삭제 admin
	@Override
	@Transactional
	public void deactivateQuestion(Long questionId) {
		Question question = findQuestionByQuestionId(questionId);
		if (question.isDeleted()) {
			throw new CustomException(ErrorCode.QUESTION_IS_DEACTIVATED);
		}
		question.deactivateEntity();
		question.updateStatusDelete();
	}

	// 삭제된 질문 복구 admin
	@Override
	@Transactional
	public void restoreQuestion(Long questionId) {
		Question question = findQuestionByQuestionId(questionId);
		if (!question.isDeleted()) {
			throw new CustomException(ErrorCode.QUESTION_IS_NOT_DEACTIVATED);
		}
		question.restoreEntity();
		question.updateStatusActive();
	}

	// 관리자가 삭제된 질문 조회 admin
	@Override
	public Page<QuestionGetDeletedResponse> findQuestionByDeleted(Pageable pageable) {
		Page<Question> questionList = questionRepository.findQuestionByDeleted(pageable);

		return questionList
			.map(QuestionGetDeletedResponse::from);
	}
}
