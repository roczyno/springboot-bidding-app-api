package com.roczyno.bidding.app.api.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthResponse(
        String jwt,
        String message
) {


}
