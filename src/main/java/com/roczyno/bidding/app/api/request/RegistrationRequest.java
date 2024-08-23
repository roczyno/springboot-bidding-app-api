package com.roczyno.bidding.app.api.request;

import com.roczyno.bidding.app.api.util.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotNull(message = AppConstants.CANNOT_BE_NULL)
    @Size(min = 3, max = 20, message = AppConstants.NAME_SIZE_MESSAGE)
    @Pattern(regexp = AppConstants.USERNAME_PATTERN, message = AppConstants.USERNAME_PATTERN_MESSAGE)
    private String firstName;
    @NotNull(message = AppConstants.CANNOT_BE_NULL)
    @Size(min = 3, max = 20, message = AppConstants.NAME_SIZE_MESSAGE)
    @Pattern(regexp = AppConstants.USERNAME_PATTERN, message = AppConstants.USERNAME_PATTERN_MESSAGE)
    private String lastName;
    @Email(message = AppConstants.EMAIL)
    @NotNull(message = AppConstants.CANNOT_BE_NULL)
    private String email;
    @NotNull(message = AppConstants.CANNOT_BE_NULL)
    @Pattern(regexp = AppConstants.PASSWORD_PATTERN,message = AppConstants.PASSWORD_PATTERN_MESSAGE)
    private String password;
}
