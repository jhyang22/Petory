package com.study.petory.common.util;

import org.springframework.stereotype.Component;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.repository.AlbumRepository;
import com.study.petory.domain.calender.entity.Calender;
import com.study.petory.domain.calender.repository.CalenderRepository;
import com.study.petory.domain.dailyQna.repository.DailyQnaRepository;
import com.study.petory.domain.dailyQna.repository.QuestionRepository;
import com.study.petory.domain.faq.entity.Faq;
import com.study.petory.domain.faq.repository.FaqRepository;
import com.study.petory.domain.feedback.entity.Feedback;
import com.study.petory.domain.feedback.repository.FeedbackRepository;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardRepository;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.repository.PetRepository;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
public class EntityFetcher {

	private final AlbumRepository albumRepository;
	private final CalenderRepository calenderRepository;
	private final DailyQnaRepository dailyQnaRepository;
	private final FaqRepository faqRepository;
	private final FeedbackRepository feedbackRepository;
	private final OwnerBoardRepository ownerBoardRepository;
	private final PetRepository petRepository;
	private final PlaceRepository placeRepository;
	private final TradeBoardRepository tradeBoardRepository;
	private final UserRepository userRepository;
	private final QuestionRepository questionRepository;

	public Album findAlbumByAlbumId(Long albumId) {
		return albumRepository.findById(albumId)
			.orElseThrow(() -> new CustomException(ErrorCode.ALBUM_NOT_FOUND));
	}

	public Calender findCalenderByCalenderId(Long calenderId) {
		return calenderRepository.findById(calenderId)
			.orElseThrow(() -> new CustomException(ErrorCode.CALENDER_NOT_FOUND));
	}

	public Faq findFaqByFaqId(Long faqId) {
		return faqRepository.findById(faqId)
			.orElseThrow(() -> new CustomException(ErrorCode.FAQ_QNA_NOT_FOUND));
	}

	public Feedback findFeedbackByFeedbackId(Long feedbackId) {
		return feedbackRepository.findById(feedbackId)
			.orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));
	}

	public OwnerBoard findOwnerBoardByOwnerBoardId(Long ownerBoardId) {
		return ownerBoardRepository.findById(ownerBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.OWNER_BOARD_NOT_FOUND));
	}

	public Pet findPetByPetId(Long petId) {
		return petRepository.findById(petId)
			.orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
	}

	public TradeBoard findTradeBoardByTradeBoardId(Long tradeBoardId) {
		return tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));
	}

	public User findUserByUserId(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
