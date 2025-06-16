package com.study.petory.domain.dailyQna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.dailyQna.entity.QQuestion;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionCustomRepositoryImpl implements QuestionCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QQuestion qQuestion = QQuestion.question1;

	@Override
	public boolean existsByDate(String date) {
		return jpaQueryFactory
			.selectOne()
			.from(qQuestion)
			.where(
				qQuestion.date.eq(date)
			)
			.fetchFirst() != null;
	}

	@Override
	public Page<Question> findQuestionByPage(Pageable pageable) {
		List<Question> list = jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.questionStatus.in(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qQuestion.count())
			.from(qQuestion)
			.fetchOne();
		if (total == null) {
			total = 0L;
		}
		return new PageImpl<>(list, pageable, total);
	}

	@Override
	public Optional<Question> findTodayQuestion(String date) {
		return jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.date.eq(date),
				qQuestion.questionStatus.eq(QuestionStatus.ACTIVE)
			)
			.fetch()
			.stream().findAny();
	}

	@Override
	public Optional<Question> findQuestionByActive(Long questionId) {
		return jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.id.eq(questionId),
				qQuestion.questionStatus.eq(QuestionStatus.ACTIVE)
			)
			.stream().findAny();
	}

	public Page<Question> findQuestionByInactive(Pageable pageable) {
		List<Question> questionPage = jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.questionStatus.eq(QuestionStatus.INACTIVE)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qQuestion.count())
			.from(qQuestion)
			.where(
				qQuestion.questionStatus.eq(QuestionStatus.INACTIVE)
			)
			.fetchOne();
		if (total == null) {
			total = 0L;
		}
		return new PageImpl<>(questionPage, pageable, total);
	}

	@Override
	public Page<Question> findQuestionByDeleted(Pageable pageable) {
		List<Question> questionPage = jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.questionStatus.eq(QuestionStatus.DELETED)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qQuestion.count())
			.from(qQuestion)
			.where(
				qQuestion.questionStatus.eq(QuestionStatus.DELETED)
			)
			.fetchOne();
		if (total == null) {
			total = 0L;
		}
		return new PageImpl<>(questionPage, pageable, total);
	}

	public Optional<Question> findQuestionByActiveOrInactive(Long questionId) {
		return jpaQueryFactory
			.selectFrom(qQuestion)
			.where(
				qQuestion.id.eq(questionId),
				qQuestion.questionStatus.in(QuestionStatus.ACTIVE, QuestionStatus.INACTIVE)
			)
			.fetch()
			.stream().findAny();
	}
}
