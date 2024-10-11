package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.UserException;
import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.ChatRepository;
import com.roczyno.bidding.app.api.repository.MessageRepository;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.request.SendMessageRequest;
import com.roczyno.bidding.app.api.util.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Service implementation for managing messages within chats.
 * This service allows users to send messages, retrieve messages from a chat,
 * find messages by ID, and delete messages.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;
	private final MessageMapper mapper;

	/**
	 * Sends a message from a user to a specific chat.
	 *
	 * @param req the request containing the user ID, chat ID, and message content.
	 * @return a MessageResponse containing the saved message details.
	 * @throws UserException if the user is not found.
	 * @throws RuntimeException if the chat is not found.
	 */
	@Override
	public MessageResponse sendMessage(SendMessageRequest req) {
		User user= userRepository.findById(req.userId()).orElseThrow(()-> new UserException("User not found"));
		Chat chat= chatRepository.findById(req.chatId()).orElseThrow(()-> new RuntimeException("sjkj"));
		Message message= Message.builder()
				.chat(chat)
				.user(user)
				.createdAt(LocalDateTime.now())
				.content(req.content())
				.build();
		return mapper.toMessageResponse( messageRepository.save(message));
	}

	/**
	 * Retrieves all messages in a chat.
	 *
	 * @param chatId the ID of the chat whose messages are to be retrieved.
	 * @param reqUser the authenticated user making the request.
	 * @return a list of MessageResponse objects containing the chat messages.
	 * @throws RuntimeException if the chat is not found.
	 */
	@Override
	public List<MessageResponse> getChatsMessages(Integer chatId, Authentication reqUser) {
		User user= (User) reqUser.getPrincipal();
		Chat chat= chatRepository.findById(chatId).orElseThrow(()-> new RuntimeException("sjkj"));
//		if(!chat.getUsers().contains(user)){
//			throw  new ChatException("You are not allowed to view this chat");
//		}
		List<Message> messages= messageRepository.findByChatId(chat.getId());
		return messages.stream().map(mapper::toMessageResponse).toList();
	}

	/**
	 * Finds a message by its ID.
	 *
	 * @param id the ID of the message.
	 * @return a MessageResponse containing the message details.
	 * @throws RuntimeException if the message is not found.
	 */
	@Override
	public MessageResponse findMessageById(Integer id) {
		var message=messageRepository.findById(id).orElseThrow(null);
		return mapper.toMessageResponse(message);
	}

	/**
	 * Deletes a message by its ID if the authenticated user is the author of the message.
	 *
	 * @param id the ID of the message to delete.
	 * @param reqUser the authenticated user making the deletion request.
	 * @return a confirmation message upon successful deletion.
	 * @throws RuntimeException if the user is not allowed to delete the message.
	 */
	@Override
	public String deleteMessageById(Integer id, Authentication reqUser) {
		User user=(User) reqUser.getPrincipal();
		MessageResponse message=findMessageById(id);
		if(!message.userId().equals(user.getId())){
			throw new RuntimeException("You are not allowed to delete this message");
		}
		messageRepository.deleteById(id);
		return "Deleted Message";
	}
}
