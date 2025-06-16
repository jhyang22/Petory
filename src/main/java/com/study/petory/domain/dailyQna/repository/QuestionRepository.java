package com.study.petory.domain.dailyQna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionCustomRepository {
}
