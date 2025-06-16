package com.study.petory.domain.dailyQna.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionCustomRepository {

	boolean existsByDate(String date);

	Page<Question> findQuestionByPage(Pageable pageable);

	Optional<Question> findTodayQuestion(String date);

	Optional<Question> findQuestionByActive(Long questionId);

	Optional<Question> findQuestionByActiveOrInactive(Long questionId);

	Page<Question> findQuestionByInactive(Pageable pageable);

	Page<Question> findQuestionByDeleted(Pageable pageable);
}
