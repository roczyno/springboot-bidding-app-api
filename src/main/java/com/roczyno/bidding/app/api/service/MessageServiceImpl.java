package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.ChatException;
import com.roczyno.bidding.app.api.exception.UserException;
import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.MessageRepository;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.request.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChatService chatService;

	@Override
	public Message sendMessage(SendMessageRequest req) {
		User user= userRepository.findById(req.userId()).orElseThrow(()-> new UserException("User not found"));
		Chat chat= chatService.findChatById(req.chatId());
		Message message= Message.builder()
				.chat(chat)
				.user(user)
				.createdAt(LocalDateTime.now())
				.content(req.content())
				.build();
		return messageRepository.save(message);
	}

	@Override
	public List<Message> getChatsMessages(Integer chatId, Authentication reqUser) {
		User user= (User) reqUser.getPrincipal();
		Chat chat=chatService.findChatById(chatId);
		if(!chat.getUsers().contains(user)){
			throw  new ChatException("You are not allowed to view this chat");
		}
		return messageRepository.findByChatId(chat.getId());
	}

	@Override
	public Message findMessageById(Integer id) {
		return messageRepository.findById(id).orElse(null);
	}

	@Override
	public String deleteMessageById(Integer id, Authentication reqUser) {
		User user=(User) reqUser.getPrincipal();
		Message message=findMessageById(id);

		if(!message.getUser().getId().equals(user.getId())){
			throw new RuntimeException("You are not allowed to delete this message");
		}
		messageRepository.deleteById(id);
		return "Deleted Message";
	}
}
