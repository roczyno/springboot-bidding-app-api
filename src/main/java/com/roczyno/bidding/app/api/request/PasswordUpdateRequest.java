package com.roczyno.bidding.app.api.request;

public record PasswordUpdateRequest(
        String password,
        String repeatPassword
) {
}
