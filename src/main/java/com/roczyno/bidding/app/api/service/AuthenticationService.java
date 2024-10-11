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
import com.roczyno.bidding.app.api.util.UserMapper;
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
/**
 * Service class for managing user authentication, registration, password management, and account activation.
 */
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
    private final UserMapper userMapper;


    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;

    /**
     * Registers a new user with the provided registration request.
     * The user is created with an encoded password, a default role, and a subscription.
     * After user creation, an account activation email is sent.
     *
     * @param req the registration request containing user details.
     * @return a success message indicating that the user was created.
     * @throws RuntimeException if a user with the provided email already exists or if email sending fails.
     */
    @Transactional
    public String register(RegistrationRequest req)  {
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
                .enabled(true)
                .roles(List.of(userRole))
                .build();
        var savedUser=userRepository.save(user);
        subscriptionService.createSubscription(savedUser);
		try {
			sendValidationEmail(savedUser);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return "user created successfully";
    }

    /**
     * Sends an account activation email to the user after registration.
     * Generates an activation token and sends the email using the email service.
     *
     * @param user the newly registered user.
     * @throws MessagingException if an error occurs while sending the email.
     */
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

    /**
     * Generates and saves an activation token for the specified user.
     *
     * @param user the user for whom the token is being generated.
     * @return the generated activation token.
     */
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


    /**
     * Authenticates a user using the provided credentials and returns a JWT.
     *
     * @param req the authentication request containing the user's email and password.
     * @return an AuthResponse containing the JWT and user details.
     * @throws RuntimeException if the user does not exist or the credentials are incorrect.
     */
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
                .user(userMapper.toUserResponse(user))
                .message("User login successful")
                .build();
    }



    /**
     * Activates a user account using the provided token.
     * If the token is expired, a new activation email is sent.
     *
     * @param token the activation token.
     * @throws RuntimeException if the token is expired or invalid.
     */
    @Transactional
    public void activateAccount(String token)  {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow();
        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
			try {
				sendValidationEmail(savedToken.getUser());
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException("account activation failed. a new token has been sent");
        }
        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow();
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    /**
     * Initiates a password reset process by generating a reset token and sending it via email.
     *
     * @param req the password reset request containing the user's email.
     * @return a success message indicating that the reset process was initiated.
     * @throws RuntimeException if the user's email is not found or if the user is not verified.
     */
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

    /**
     * Validates the password reset token for the specified email.
     *
     * @param token the reset token.
     * @param email the user's email.
     * @return a success message if the token is valid.
     * @throws RuntimeException if the token is invalid or expired.
     */
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


    /**
     * Updates the user's password after a successful password reset token validation.
     *
     * @param req the password update request containing the new password and its confirmation.
     * @param email the user's email.
     * @return a success message indicating that the password was updated.
     * @throws RuntimeException if the passwords do not match, the email is not found, or the user is not verified.
     */
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

    /**
     * Changes the authenticated user's password.
     * Validates the old password and ensures that the new password and confirmation match.
     *
     * @param req the request containing the old password, new password, and confirmation.
     * @param connectedUser the authenticated user whose password is being changed.
     * @return a success message indicating that the password was changed.
     * @throws RuntimeException if the old password is incorrect or the new passwords do not match.
     */
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
