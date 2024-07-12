package com.roczyno.bidding.app.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.roczyno.bidding.app.api.model.User;

import java.time.LocalDateTime;
import java.util.Set;

public record ChatResponse(
		Integer id,
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="GMT")
		LocalDateTime createdAt,
		Set<UserProfileResponse> users
		)  {
}
