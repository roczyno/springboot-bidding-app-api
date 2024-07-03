package com.roczyno.bidding.app.api.response;

public record PaymentResponse(
		boolean status,
		String message,
		Data data
) {
	public record Data(
			String authorization_url,
			String access_code,
			String reference
	) {}
}
