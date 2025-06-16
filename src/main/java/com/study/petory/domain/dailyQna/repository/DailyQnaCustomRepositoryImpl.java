package com.study.petory.domain.dailyQna.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.QDailyQna;
import com.study.petory.domain.dailyQna.entity.QQuestion;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyQnaCustomRepositoryImpl implements DailyQnaCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QDailyQna qDailyQna = QDailyQna.dailyQna;

	private final QUser qUser = QUser.user;

	private final QQuestion qQuestion = QQuestion.question1;

	@Override
	public List<DailyQna> findDailyQna(Long userId, Long questionId) {
		return jpaQueryFactory
			.selectFrom(qDailyQna)
			.join(qDailyQna.user, qUser).fetchJoin()
			.join(qDailyQna.question, qQuestion).fetchJoin()
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.question.id.eq(questionId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.ACTIVE)
			)
			.orderBy(qDailyQna.createdAt.desc())
			.fetch();
	}

	@Override
	public Optional<DailyQna> findDailyQnaByActive(Long dailyQnaId) {
		return jpaQueryFactory
			.selectFrom(qDailyQna)
			.where(
				qDailyQna.id.eq(dailyQnaId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.ACTIVE)
			)
			.stream().findAny();
	}

	@Override
	public Page<DailyQna> findDailyQnaByHidden(Long userId, Pageable pageable) {
		List<DailyQna> dailyQnaList = jpaQueryFactory
			.selectFrom(qDailyQna)
			.join(qDailyQna.user, qUser).fetchJoin()
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.HIDDEN)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qDailyQna.count())
			.from(qDailyQna)
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.HIDDEN)
			)
			.fetchOne();
		if (total == null) {
			total = 0L;
		}
		return new PageImpl<>(dailyQnaList, pageable, total);
	}

	@Override
	public Page<DailyQna> findDailyQnaByDeleted(Long userId, Pageable pageable) {
		List<DailyQna> dailyQnaList = jpaQueryFactory
			.selectFrom(qDailyQna)
			.join(qDailyQna.user, qUser).fetchJoin()
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.DELETED)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qDailyQna.count())
			.from(qDailyQna)
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.DELETED)
			)
			.fetchOne();
		if (total == null) {
			total = 0L;
		}
		return new PageImpl<>(dailyQnaList, pageable, total);
	}

	@Override
	public boolean isDailyQnaToday(Long userId, Long questionId) {
		LocalDate today = LocalDate.now();
		return jpaQueryFactory
			.selectOne()
			.from(qDailyQna)
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.question.id.eq(questionId),
				qDailyQna.createdAt.between(
					today.atStartOfDay(),
					today.plusDays(1).atStartOfDay().minusNanos(1)
				)
			)
			.fetchFirst() != null;
	}
}
