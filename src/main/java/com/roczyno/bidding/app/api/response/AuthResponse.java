package com.roczyno.bidding.app.api.response;

import com.roczyno.bidding.app.api.model.User;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthResponse(
        String jwt,
		Integer userId,
		String email,
        String message

) {


}
