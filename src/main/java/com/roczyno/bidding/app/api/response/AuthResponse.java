package com.roczyno.bidding.app.api.response;
import lombok.Builder;

@Builder
public record AuthResponse(
        String jwt,
		UserProfileResponse user,
       String message

) {


}
