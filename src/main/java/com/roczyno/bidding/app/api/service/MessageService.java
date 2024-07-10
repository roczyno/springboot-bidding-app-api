package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.request.SendMessageRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MessageService {
	MessageResponse sendMessage(SendMessageRequest req) ;
	List<MessageResponse> getChatsMessages(Integer chatId, Authentication reqUser);
	MessageResponse findMessageById(Integer id);
	String deleteMessageById(Integer id,Authentication reqUser);
}
