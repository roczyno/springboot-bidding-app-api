package com.roczyno.bidding.app.api.controller;


import com.roczyno.bidding.app.api.request.AuthRequest;
import com.roczyno.bidding.app.api.request.PasswordResetRequest;
import com.roczyno.bidding.app.api.request.PasswordUpdateRequest;
import com.roczyno.bidding.app.api.request.RegistrationRequest;
import com.roczyno.bidding.app.api.request.changePasswordRequest;
import com.roczyno.bidding.app.api.response.AuthResponse;
import com.roczyno.bidding.app.api.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegistrationRequest req) {
        return ResponseEntity.ok(authenticationService.register(req));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.ok(authenticationService.login(req));
    }
    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token)  {
        authenticationService.activateAccount(token);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> initiateForgotPassword(@Valid @RequestBody PasswordResetRequest req) {
        return ResponseEntity.ok(authenticationService.initiateForgotPassword(req));
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<Object> verifyOtp(@RequestParam int token,@RequestParam String email) {
        return ResponseEntity.ok(authenticationService.validatePasswordResetToken(token,email));
    }
    @PostMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody PasswordUpdateRequest req, @RequestParam String email) {
        return ResponseEntity.ok(authenticationService.updatePassword(req,email));
    }

    @PostMapping ("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody changePasswordRequest req, Authentication connectedUser){
        return ResponseEntity.ok(authenticationService.changePassword(req,connectedUser));
    }


}
