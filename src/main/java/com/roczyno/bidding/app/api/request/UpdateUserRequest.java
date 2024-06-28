package com.roczyno.bidding.app.api.request;

public record UpdateUserRequest(
		String firstName,
		String lastName,
		String profilePic,
		String phone
) {
}
