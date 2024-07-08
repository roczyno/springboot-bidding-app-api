package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {
	Chat createChat(Authentication reqUser, Integer userId2) ;
	Chat findChatById(Integer id) ;
	List<Chat> findAllChatsByUserId(Authentication connectedUser);
}
