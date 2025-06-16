package com.study.petory.domain.dailyQna.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionService {

	void setQuestion();

	Question findQuestionByQuestionId(Long questionId);

	Question findQuestionByActive(Long questionId);

	Question findQuestionByActiveOrInactive(Long questionId);

	void existsByDate(String date);

	void saveQuestion(QuestionCreateRequestDto request);

	Page<QuestionGetAllResponseDto> findAllQuestion(Pageable pageable);

	QuestionGetOneResponseDto findOneQuestion(Long questionId);

	QuestionGetTodayResponseDto findTodayQuestion();

	void updateQuestion(Long questionId, QuestionUpdateRequestDto request);

	void inactiveQuestion(Long questionId);

	Page<QuestionGetInactiveResponseDto> findInactiveQuestion(Pageable pageable);

	void updateQuestionStatusActive(Long questionId);

	void deactivateQuestion(Long questionId);

	Page<QuestionGetDeletedResponse> findQuestionByDeleted(Pageable pageable);

	void restoreQuestion(Long questionId);
}
