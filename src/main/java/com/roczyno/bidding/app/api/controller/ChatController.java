package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.service.ChatService;
import com.roczyno.bidding.app.api.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("chat")
public class ChatController {
	private final ChatService chatService;

	@PostMapping("/create/{userId}")
	public ResponseEntity<Object> createChat(@PathVariable Integer userId, Authentication connectedUser){
		return ResponseHandler.successResponse(chatService.createChat(connectedUser,userId), HttpStatus.OK);
	}

	@GetMapping("/{chatId}")
	public ResponseEntity<Object> getChat(@PathVariable Integer chatId){
		return ResponseHandler.successResponse(chatService.findChatById(chatId),HttpStatus.OK);
	}

	@GetMapping("/user")
	public ResponseEntity<Object> getUserChats(Authentication connectedUser) {
		return ResponseHandler.successResponse(chatService.findAllChatsByUserId(connectedUser),HttpStatus.OK);
	}
}
