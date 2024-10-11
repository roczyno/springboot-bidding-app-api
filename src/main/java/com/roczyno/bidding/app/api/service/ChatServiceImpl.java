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

/**
 * Service implementation for managing chat functionality between users.
 * This service allows users to create new chats, retrieve existing chats, and find chats by user ID.
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ChatMapper mapper;

	/**
	 * Creates a new chat between two users if it does not already exist.
	 * If a chat exists between the two users, the existing chat is returned.
	 *
	 * @param reqUser the currently authenticated user initiating the chat.
	 * @param userId2 the ID of the other user to chat with.
	 * @return a ChatResponse containing chat details.
	 * @throws UserException if the second user is not found.
	 */
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


	/**
	 * Finds a chat by its ID.
	 *
	 * @param id the ID of the chat.
	 * @return a ChatResponse containing chat details.
	 * @throws ChatException if the chat is not found.
	 */
	@Override
	public ChatResponse findChatById(Integer id) {
		return mapper.toChatResponse(chatRepository.findById(id).orElseThrow(()-> new ChatException("Chat not found")));
	}

	/**
	 * Retrieves all chats that involve the authenticated user.
	 *
	 * @param connectedUser the authenticated user whose chats are to be retrieved.
	 * @return a list of ChatResponse objects, each containing chat details.
	 */
	@Override
	public List<ChatResponse> findAllChatsByUserId(Authentication connectedUser) {
		User user=(User) connectedUser.getPrincipal();
		List<Chat> chats=chatRepository.findByUserId(user.getId());
		return chats.stream().map(mapper::toChatResponse).collect(Collectors.toList());
	}
}
