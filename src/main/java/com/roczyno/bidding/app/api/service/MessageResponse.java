package com.roczyno.bidding.app.api.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.response.ChatResponse;
import com.roczyno.bidding.app.api.response.UserProfileResponse;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public record MessageResponse(
		 Integer id,
		 String content,
		 @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="GMT")
		 LocalDateTime createdAt,
		 Integer userId

) {
}
