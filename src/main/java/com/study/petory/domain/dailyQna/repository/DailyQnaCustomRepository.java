package com.study.petory.domain.dailyQna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyQna.entity.DailyQna;

public interface DailyQnaCustomRepository {

	Optional<DailyQna> findDailyQnaByActive(Long dailyQnaId);

	Page<DailyQna> findDailyQnaByHidden(Long userId, Pageable pageable);

	Page<DailyQna> findDailyQnaByDeleted(Long userId, Pageable pageable);

	boolean isDailyQnaToday(Long userId, Long questionId);

	List<DailyQna> findDailyQna(Long userId, Long questionId);
}
