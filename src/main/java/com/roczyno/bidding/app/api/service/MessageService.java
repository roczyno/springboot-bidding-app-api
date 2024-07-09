package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.request.SendMessageRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MessageService {
	Message sendMessage(SendMessageRequest req) ;
	List<Message> getChatsMessages(Integer chatId, Authentication reqUser);
	Message findMessageById(Integer id);
	String deleteMessageById(Integer id,Authentication reqUser);
}
