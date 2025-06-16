package com.study.petory.domain.dailyQna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyQna;

public interface DailyQnaRepository extends JpaRepository<DailyQna, Long>, DailyQnaCustomRepository {
}
