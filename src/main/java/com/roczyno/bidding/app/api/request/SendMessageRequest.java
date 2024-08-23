package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
		Integer userId,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String content,
		Integer chatId
) {
}
