package com.roczyno.bidding.app.api.request;

public record AuthRequest(
        String email,
        String password
) {
}
