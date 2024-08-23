package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateBidRequest(
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
        long amount
) {
}
