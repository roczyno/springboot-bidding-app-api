package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.SendMessageRequest;
import com.roczyno.bidding.app.api.service.MessageResponse;
import com.roczyno.bidding.app.api.service.MessageService;
import com.roczyno.bidding.app.api.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("message")
public class MessageController {
	private final MessageService messageService;

	@PostMapping
	public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest req){
		return new ResponseEntity<>(messageService.sendMessage(req), HttpStatus.OK);
	}

	@GetMapping("/chat/{chatId}")
	public ResponseEntity<List<MessageResponse>> getChatMessages(@PathVariable Integer chatId, Authentication connectedUser){
		return new ResponseEntity<>(messageService.getChatsMessages(chatId,connectedUser),HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> deleteMessage(@PathVariable Integer id,Authentication connectedUser){
		return ResponseHandler.successResponse(messageService.deleteMessageById(id,connectedUser),HttpStatus.OK);
	}
}
