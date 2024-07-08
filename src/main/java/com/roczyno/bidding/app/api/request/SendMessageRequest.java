package com.roczyno.bidding.app.api.request;

public record SendMessageRequest(
		Integer userId,
		String content,
		Integer chatId
) {
}
