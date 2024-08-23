package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		@Size(min = 3, max = 20, message = AppConstants.NAME_SIZE_MESSAGE)
		@Pattern(regexp = AppConstants.USERNAME_PATTERN, message = AppConstants.USERNAME_PATTERN_MESSAGE)
		String firstName,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		@Size(min = 3, max = 20, message = AppConstants.NAME_SIZE_MESSAGE)
		@Pattern(regexp = AppConstants.USERNAME_PATTERN, message = AppConstants.USERNAME_PATTERN_MESSAGE)
		String lastName,
		String profilePic,
		@Pattern(regexp = AppConstants.PHONE_PATTERN,message = AppConstants.PHONE_PATTERN_MESSAGE)
		String phone
) {
}
