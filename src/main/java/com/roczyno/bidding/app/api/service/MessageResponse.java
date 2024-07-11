package com.roczyno.bidding.app.api.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.roczyno.bidding.app.api.response.ChatResponse;

import java.time.LocalDateTime;

public record MessageResponse(
		 Integer id,
		 String content,
		 @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="GMT")
		 LocalDateTime createdAt,
		 ChatResponse chat,
		 Integer userId

) {
}
