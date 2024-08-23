package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CreateAuctionRequest(

		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String title,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		LocalDate startDate,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		LocalDate endDate,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String distanceCv,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String location,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String modelColor,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String transmission,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		String engineType,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		long startingBid,
		@NotNull(message = AppConstants.CANNOT_BE_NULL)
		long buyNowPrice,
		long currentBid,
		List<String> images


) {
}
