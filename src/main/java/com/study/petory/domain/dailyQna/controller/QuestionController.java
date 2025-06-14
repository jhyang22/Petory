package com.study.petory.domain.dailyQna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.DailyQnaUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.request.QuestionUpdateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetHiddenResponse;
import com.study.petory.domain.dailyQna.dto.response.DailyQnaGetResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetDeletedResponse;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetInactiveResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetTodayResponseDto;
import com.study.petory.domain.dailyQna.service.DailyQnaService;
import com.study.petory.domain.dailyQna.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final DailyQnaService dailyQnaService;
	private final QuestionService questionService;

	@PostMapping("/test")
	public ResponseEntity<CommonResponse<Void>> test() {
		questionService.setQuestion();
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문 생성				관리자만 가능
	 * adminId				관리자인지 검증
	 * @param request       관리자가 추가하는 질문
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<Void>> createQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@Valid @RequestBody QuestionCreateRequestDto request
	) {
		Long adminId = 1L;
		questionService.saveQuestion(adminId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문 전체 조회			관리자만 가능
	 * adminId				관리자인지 검증
	 * @param pageable		한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return CommonResponse 성공 메세지, data: 질문, 날짜, 비활성화 시 비활성화 날짜 표시
	 * 											질문을 날짜 기준 내림차순으로 조회, 한 페이지에 50개씩 출력
	 */
	@GetMapping("/all")
	public ResponseEntity<CommonResponse<Page<QuestionGetAllResponseDto>>> getAllQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PageableDefault(size = 50, sort = "date", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Long adminId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, questionService.findAllQuestion(adminId, pageable));
	}

	/**
	 * 질문 단건 조회			관리자만 가능
	 * adminId				관리자인지 검증
	 * @param questionId	조회할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 비활성화 시 비활성화 날짜 표시
	 */
	@GetMapping("/{questionId}")
	public ResponseEntity<CommonResponse<QuestionGetOneResponseDto>> getOneQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId
	) {
		Long adminId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, questionService.findOneQuestion(adminId, questionId));
	}

	/**
	 * 오늘의 질문 조회		모든 권한 사용 가능
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜
	 */
	@GetMapping("/today")
	public ResponseEntity<CommonResponse<QuestionGetTodayResponseDto>> getTodayQuestion(
	) {
		QuestionGetTodayResponseDto dto = questionService.findTodayQuestion();
		return CommonResponse.of(SuccessCode.FOUND, dto);
	}

	/**
	 * 질문 수정				관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param questionId	수정할 질문의 id
	 * @param request		수정할 내용
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PutMapping("/{questionId}")
	public ResponseEntity<CommonResponse<Void>> updateQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId,
		@Valid @RequestBody QuestionUpdateRequestDto request
		) {
		Long adminId = 1L;
		questionService.updateQuestion(adminId ,questionId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 질문 비활성화			관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param questionId	비활성화 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{questionId}/inactive")
	public ResponseEntity<CommonResponse<Void>> InactiveQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId
	) {
		Long adminId = 1L;
		questionService.inactiveQuestion(adminId, questionId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 비활성화 된 질문 조회	관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param pageable		한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 수정일
	 */
	@GetMapping("/inactive")
	public ResponseEntity<CommonResponse<Page<QuestionGetInactiveResponseDto>>> getInactiveQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PageableDefault(size = 50, sort = "updatedAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Long adminId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, questionService.findInactiveQuestion(adminId, pageable));
	}

	/**
	 * 비활성화 된 질문 활성화	관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param questionId	활성화 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{questionId}/activate/restore")
	public ResponseEntity<CommonResponse<Void>> updateQuestionStatusActive(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId
	) {
		Long adminId = 1L;
		questionService.updateQuestionStatusActive(adminId, questionId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 질문 삭제				관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param questionId	삭제 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/{questionId}")
	public ResponseEntity<CommonResponse<Void>> deactivateQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId
	) {
		Long adminId = 1L;
		questionService.deactivateQuestion(adminId, questionId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 삭제된 질문 조회		관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param pageable		한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 질문, 날짜, 삭제일
	 */
	@GetMapping("/deleted")
	public ResponseEntity<CommonResponse<Page<QuestionGetDeletedResponse>>> getQuestionByDeleted(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PageableDefault(size = 50, sort = "deletedAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Long adminId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, questionService.findQuestionByDeleted(adminId, pageable));
	}

	/**
	 * 질문 복구				관리자만 가능
	 * adminId 				관리자인지 검증
	 * @param questionId	복구 할 질문의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{questionId}/restore")
	public ResponseEntity<CommonResponse<Void>> restoreQuestion(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long questionId
	) {
		Long adminId = 1L;
		questionService.restoreQuestion(adminId, questionId);
		return CommonResponse.of(SuccessCode.RESTORED);
	}

	/**
	 * 답변 등록
	 * userId				답변을 작성한 유저
	 * @param questionId    유저가 답변을 작성한 질문의 Id
	 * @param request        유저가 작성한 답변
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PostMapping("/{questionId}/daily-qnas")
	public ResponseEntity<CommonResponse<Void>> createDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId,
		@Valid @RequestBody DailyQnaCreateRequestDto request
	) {
		Long userId = 1L;
		dailyQnaService.saveDailyQna(userId, questionId, request);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 질문의 답변 조회
	 * userId				답변을 작성한 유저
	 * @param questionId    질문 Id로 유저가 작성한 답변을 검색
	 * @return CommonResponse 성공 메세지, data: 작성일 기준 내림차순 답변 조회
	 */
	@GetMapping("/{questionId}/daily-qnas")
	public ResponseEntity<CommonResponse<List<DailyQnaGetResponseDto>>> getDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long questionId
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, dailyQnaService.findDailyQna(userId, questionId));
	}

	/**
	 * 답변 수정
	 * userId				답변을 작성한 유저
	 * @param dailyQnaId    유저가 수정할 답변의 id
	 * @param request        유저가 작성한 수정 할 내용
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-qnas/{dailyQnaId}")
	public ResponseEntity<CommonResponse<Void>> updateDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long dailyQnaId,
		@Valid @RequestBody DailyQnaUpdateRequestDto request
	) {
		Long userId = 1L;
		dailyQnaService.updateDailyQna(userId, dailyQnaId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 답변 숨김
	 * userId				답변을 작성한 유저
	 * @param dailyQnaId	숨길 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-qnas/{dailyQnaId}/hide")
	public ResponseEntity<CommonResponse<Void>> hideDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long dailyQnaId
	) {
		Long userId = 1L;
		dailyQnaService.hideDailyQna(userId, dailyQnaId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 숨긴 답변 조회
	 * userId				삭제할 답변의 유저 id
	 * @param pageable		한 번에 10개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 답변, 생성일, 숨긴일
	 */
	@GetMapping("/daily-qnas/hide")
	public ResponseEntity<CommonResponse<Page<DailyQnaGetHiddenResponse>>> getHiddenDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, dailyQnaService.findHiddenDailyQna(userId, pageable));
	}

	/**
	 * 숨긴 답변 복구
	 * userId				복구할 답변의 유저 id
	 * @param dailyQnaId	복구할 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-qnas/{dailyQnaId}/visibility/restore")
	public ResponseEntity<CommonResponse<Void>> updateDailyQnaStatusActive(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PathVariable Long dailyQnaId
	) {
		Long userId = 1L;
		dailyQnaService.updateDailyQnaStatusActive(userId, dailyQnaId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 답변 삭제				관리자만 삭제 가능
	 * adminId				관리자인지 검증
	 * userId				삭제할 답변의 유저 Id
	 * @param dailyQnaId    관리자가 삭제할 답변의 id
	 * @return CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/daily-qnas/{dailyQnaId}")
	public ResponseEntity<CommonResponse<Void>> deleteDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long dailyQnaId
	) {
		Long adminId = 1L;
		dailyQnaService.deleteDailyQna(adminId, dailyQnaId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 유저의 삭제된 답변 조회	관리자만 조회 가능
	 * adminId				관리자인지 검증
	 * userId				조회할 유저의 id
	 * @param pageable		한 번에 50개씩 반환하여 원하는 페이지를 선택
	 * @return	CommonResponse 성공 메세지, data: 답변, 생성일, 삭제일
	 */
	@GetMapping("/daily-qnas/deleted")
	public ResponseEntity<CommonResponse<Page<DailyQnaGetDeletedResponse>>> getDeletedDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long userId
		@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Long userId = 1L;	// todo 어디서 받아올지 수정 필요. url이 유력
		Long adminId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, dailyQnaService.findDeletedDailyQna(adminId, userId, pageable));
	}

	/**
	 * 삭제된 답변 복구
	 * adminId				관리자인지 검증
	 * @param dailyQnaId	복구할 답변의 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/daily-qnas/{dailyQnaId}/restore")
	public ResponseEntity<CommonResponse<Void>> restoreDailyQna(
		// 유저 정보: 수정 예정
		// 어노테이션 Long adminId
		@PathVariable Long dailyQnaId
	) {
		Long adminId = 1L;
		dailyQnaService.restoreDailyQna(adminId, dailyQnaId);
		return CommonResponse.of(SuccessCode.UPDATED);
	}
}
