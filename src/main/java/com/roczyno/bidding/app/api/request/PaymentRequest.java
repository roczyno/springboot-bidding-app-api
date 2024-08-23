package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
		@Email(message = AppConstants.EMAIL)
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String email,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String amount)
{
}
