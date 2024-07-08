package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.request.SendMessageRequest;

import java.util.List;

public interface MessageService {
	Message sendMessage(SendMessageRequest req) ;
	List<Message> getChatsMessages(Integer chatId, User reqUser);
	Message findMessageById(Integer id);
	String deleteMessageById(Integer id,User reqUser);
}
