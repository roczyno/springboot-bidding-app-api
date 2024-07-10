package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.ChatException;
import com.roczyno.bidding.app.api.exception.UserException;
import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.ChatRepository;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.response.ChatResponse;
import com.roczyno.bidding.app.api.util.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ChatMapper mapper;

	@Override
	public ChatResponse createChat(Authentication reqUser, Integer userId2)  {
		User user1=(User) reqUser.getPrincipal();
		User user2= userRepository.findById(userId2)
				.orElseThrow(()->new UserException("User not found"));
		Chat isChatExist=chatRepository.findSingleChatByUserIds(user1,user2);
		if(isChatExist!=null){
			return mapper.toChatResponse(isChatExist);
		}
		Chat chat=Chat.builder()
				.createdAt(LocalDateTime.now())
				.users(new HashSet<>())
				.build();
                 chat.getUsers().add(user1);
                 chat.getUsers().add(user2);

		return mapper.toChatResponse(chatRepository.save(chat));
	}

	@Override
	public ChatResponse findChatById(Integer id) {
		return mapper.toChatResponse(chatRepository.findById(id).orElseThrow(()-> new ChatException("Chat not found")));
	}

	@Override
	public List<ChatResponse> findAllChatsByUserId(Authentication connectedUser) {
		User user=(User) connectedUser.getPrincipal();
		List<Chat> chats=chatRepository.findByUserId(user.getId());
		return chats.stream().map(mapper::toChatResponse).collect(Collectors.toList());
	}
}
