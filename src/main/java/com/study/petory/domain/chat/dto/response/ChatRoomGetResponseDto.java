package com.study.petory.domain.chat.dto.response;

import java.util.List;

import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetResponseDto {

	private final Long tradeBoardId;

	private final Long sellerId;

	private final Long customerId;

	private final List<ChatMessage> messages;

	public ChatRoomGetResponseDto(ChatRoom chatRoom) {
		this.tradeBoardId = chatRoom.getTradeBoardId();
		this.sellerId = chatRoom.getSellerId();
		this.customerId = chatRoom.getCustomerId();
		this.messages = chatRoom.getMessages();
	}
}
