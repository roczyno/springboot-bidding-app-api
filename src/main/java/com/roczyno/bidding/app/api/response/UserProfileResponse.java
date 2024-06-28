package com.roczyno.bidding.app.api.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
		String firstName,
		String lastName,
		String email,
		String profilePic,
		String phone
) {
}
