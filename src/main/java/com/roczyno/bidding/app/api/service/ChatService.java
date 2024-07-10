package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.response.ChatResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {
	ChatResponse createChat(Authentication reqUser, Integer userId2) ;
	ChatResponse findChatById(Integer id) ;
	List<ChatResponse> findAllChatsByUserId(Authentication connectedUser);
}
