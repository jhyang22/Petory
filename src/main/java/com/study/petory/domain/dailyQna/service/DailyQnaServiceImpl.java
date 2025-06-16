package com.study.petory.domain.dailyQna.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetHiddenResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.repository.DailyQnaRepository;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyQnaServiceImpl implements DailyQnaService{

	private final DailyQnaRepository dailyQnaRepository;
	private final QuestionService questionService;
	private final UserService userService;

	// 단순 답변 조회
	@Override
	public DailyQna findDailyQnaByDailyQnaId(Long dailyQnaId) {
		return dailyQnaRepository.findById(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	// 상태가 정상인 답변 조회
	@Override
	public DailyQna findDailyQnaByActive(Long dailyQnaId) {
		return dailyQnaRepository.findDailyQnaByActive(dailyQnaId)
			.orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	// 본인의 답변이 맞는지 검증
	@Override
	public void validateAuthor(Long userId, DailyQna dailyQna) {
		if (!dailyQna.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	// 사용자의 답변을 저장
	@Override
	@Transactional
	public void saveDailyQna(Long userId, Long questionId, DailyQnaCreateRequestDto requestDto) {
		if (dailyQnaRepository.isDailyQnaToday(userId, questionId)) {
			throw new CustomException(ErrorCode.ALREADY_WRITTEN_TODAY);
		}

		Question todayQuestion = questionService.findQuestionByQuestionId(questionId);
		dailyQnaRepository.save(DailyQna.builder()
			.user(userService.getUserById(userId))
			.question(todayQuestion)
			.answer(requestDto.getAnswer())
			.dailyQnaStatus(DailyQnaStatus.ACTIVE)
			.build()
		);
	}

	// 질문에 사용자가 남긴 모든 답변 조회
	@Override
	@Transactional
	public List<DailyQnaGetResponseDto> findDailyQna(Long userId, Long questionId) {
		List<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQna(userId, questionId);

		return dailyQnaList.stream()
			.map(DailyQnaGetResponseDto::from)
			.collect(Collectors.toList());
	}

	// 답변을 사용자가 수정
	@Override
	@Transactional
	public void updateDailyQna(Long userId, Long dailyQnaId, DailyQnaUpdateRequestDto requestDto) {
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateDailyQna(requestDto.getAnswer());
	}

	// 답변을 숨김 처리
	@Override
	@Transactional
	public void hideDailyQna(Long userId, Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusHidden();
	}

	// 숨김 처리한 답변 조회
	@Override
	public Page<DailyQnaGetHiddenResponse> findHiddenDailyQna(Long userId, Pageable pageable) {
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaByHidden(userId, pageable);
		return dailyQnaList
			.map(DailyQnaGetHiddenResponse::from);
	}

	// 숨김 처리한 답변 정상으로 복구
	@Override
	@Transactional
	public void updateDailyQnaStatusActive(Long userId, Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByDailyQnaId(dailyQnaId);
		validateAuthor(userId, dailyQna);
		dailyQna.updateStatusActive();
	}

	// 답변을 관리자가 삭제
	@Override
	@Transactional
	public void deleteDailyQna(Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByActive(dailyQnaId);
		dailyQna.deactivateEntity();
		dailyQna.updateStatusDelete();
	}

	// 관리자가 삭제된 답변 조회
	@Override
	public Page<DailyQnaGetDeletedResponse> findDeletedDailyQna(Long userId, Pageable pageable) {
		Page<DailyQna> dailyQnaList = dailyQnaRepository.findDailyQnaByDeleted(userId, pageable);

		return dailyQnaList
			.map(DailyQnaGetDeletedResponse::from);
	}

	// 관리자가 삭제된 답변 복구
	@Override
	@Transactional
	public void restoreDailyQna(Long dailyQnaId) {
		DailyQna dailyQna = findDailyQnaByDailyQnaId(dailyQnaId);
		dailyQna.updateStatusActive();
		dailyQna.restoreEntity();
	}
}
