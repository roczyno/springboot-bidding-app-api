package com.roczyno.bidding.app.api.service;



import com.roczyno.bidding.app.api.config.JwtService;
import com.roczyno.bidding.app.api.model.ForgotPasswordToken;
import com.roczyno.bidding.app.api.model.Token;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.ForgotPasswordTokenRepository;
import com.roczyno.bidding.app.api.repository.RoleRepository;
import com.roczyno.bidding.app.api.repository.TokenRepository;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.request.AuthRequest;
import com.roczyno.bidding.app.api.request.PasswordResetRequest;
import com.roczyno.bidding.app.api.request.PasswordUpdateRequest;
import com.roczyno.bidding.app.api.request.RegistrationRequest;
import com.roczyno.bidding.app.api.request.changePasswordRequest;
import com.roczyno.bidding.app.api.response.AuthResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private  final SubscriptionService subscriptionService;


    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public String register(RegistrationRequest req) throws MessagingException {
        User isEmailExist= userRepository.findByEmail(req.getEmail());
        if(isEmailExist!=null){
            throw new RuntimeException("User with this email already exists");
        }
        var userRole = roleRepository.findByName("USER").orElseThrow();


        var user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        var savedUser=userRepository.save(user);
        subscriptionService.createSubscription(savedUser);
        sendValidationEmail(savedUser);
        return "user created successfully";
    }


    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFirstName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "account activation"
        );


    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }


    private String generateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }


    public AuthResponse login(AuthRequest req) {
        User isUserExist= userRepository.findByEmail(req.email());
        if(isUserExist==null){
            throw new RuntimeException("Wrong credentials");
        }
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                req.email(), req.password()
        ));

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("username", user.getUsername());
        var jwt = jwtService.generateToken(claims, user);
        return AuthResponse.builder()
                .jwt(jwt)
                .userId(user.getId())
                .email(user.getEmail())
                .message("User login successful")
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow();
        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("account activation failed. a new token has been sent");
        }
        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow();
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    public String initiateForgotPassword(PasswordResetRequest req) {
        var user = userRepository.findByEmail(req.email());
        if (user == null) {
            throw new RuntimeException("email not found");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Only verified uses can request for a forgot password");
        }

        int token = generateAndSavePasswordResetToken(user);
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    user.getFirstName(),
                    EmailTemplate.ACTIVATE_ACCOUNT,
                    activationUrl,
                    String.valueOf(token),
                    "Forgot Password Otp"


            );
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "Forgot password initiated successfully. Check your email for the otp";

    }

    private int generateAndSavePasswordResetToken(User user) {
        int generatedToken = generateResetPasswordToken();
        var token = ForgotPasswordToken.builder()
                .token(generatedToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build();
        forgotPasswordTokenRepository.save(token);
        return generatedToken;

    }

    private int generateResetPasswordToken() {
        Random rand = new Random();
        return rand.nextInt(1000, 9999);
    }

    public String validatePasswordResetToken(int token,String email) {
        var user= userRepository.findByEmail(email);
        ForgotPasswordToken fp = forgotPasswordTokenRepository.findByTokenAndUser(token,user);
        if (fp == null) {
            throw new RuntimeException("Invalid token");
        }
        if (fp.getExpiredAt().isBefore(LocalDateTime.now())) {
            forgotPasswordTokenRepository.deleteById(fp.getId());
            throw new RuntimeException("Token expired");
        }

        return "Token verified successfully";

    }


    public String updatePassword(PasswordUpdateRequest req, String email) {
        String password = req.password();
        String repeatPassword = req.repeatPassword();
        if (!password.matches(repeatPassword)) {
            throw new RuntimeException("Passwords do not match");
        }
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email not found");
        }
        if (!user.isEnabled()) {
            throw new RuntimeException("Only verified uses can request for a forgot password");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "Password updated successfully";
    }

    public String changePassword(changePasswordRequest req, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        String oldPassword = req.oldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if(!req.newPassword() .equals(req.confirmPassword())){
            throw new RuntimeException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }
}
