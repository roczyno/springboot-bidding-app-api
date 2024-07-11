package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RealTimeChat {

	private static final Logger logger = LoggerFactory.getLogger(RealTimeChat.class);
	private final SimpMessagingTemplate template;

	@MessageMapping("/chat.sendMessage")
	public void receiveMessage(@Payload SendMessageRequest message) {
		logger.info("Received message: {}", message);
		template.convertAndSend("/topic/chat" + message.chatId(), message);
		logger.info("Message sent to /topic/chat{}", message.chatId());
	}
}
