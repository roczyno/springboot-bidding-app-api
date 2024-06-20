package com.roczyno.bidding.app.api.request;

public record changePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}
