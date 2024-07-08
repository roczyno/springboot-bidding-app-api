package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.ChatException;
import com.roczyno.bidding.app.api.exception.UserException;
import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.ChatRepository;
import com.roczyno.bidding.app.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	@Override
	public Chat createChat(Authentication reqUser, Integer userId2)  {
		User user1=(User) reqUser.getPrincipal();
		User user2= userRepository.findById(userId2)
				.orElseThrow(()->new UserException("User not found"));
		Chat isChatExist=chatRepository.findSingleChatByUserIds(user1,user2);
		if(isChatExist!=null){
			return isChatExist;
		}
		Chat chat=Chat.builder()
				.createdAt(LocalDateTime.now())
				.users(new HashSet<>())
				.build();
                 chat.getUsers().add(user1);
                 chat.getUsers().add(user2);

		return chatRepository.save(chat);
	}

	@Override
	public Chat findChatById(Integer id) {
		return chatRepository.findById(id).orElseThrow(()-> new ChatException("Chat not found"));
	}

	@Override
	public List<Chat> findAllChatsByUserId(Integer userId) {
		return chatRepository.findByUserId(userId);
	}
}
