package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthRequest(
		@Email(message = AppConstants.EMAIL)
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
        String email,
		@Pattern(regexp = AppConstants.PASSWORD_PATTERN,message = AppConstants.PASSWORD_PATTERN_MESSAGE)
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
        String password
) {
}
