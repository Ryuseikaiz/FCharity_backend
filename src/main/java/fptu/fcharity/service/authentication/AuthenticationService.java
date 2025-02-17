package fptu.fcharity.service.authentication;


import fptu.fcharity.dto.authentication.ResetPasswordDto;
import fptu.fcharity.dto.authentication.LoginUserDto;
import fptu.fcharity.dto.authentication.RegisterUserDto;
import fptu.fcharity.dto.authentication.VerifyUserDto;
import fptu.fcharity.entity.User;
import fptu.fcharity.exception.ApiRequestException;
import fptu.fcharity.repository.UserRepository;
import fptu.fcharity.service.UserService;
import fptu.fcharity.service.helper.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userService = userService;
    }

    public User signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new ApiRequestException("Email already exists");
        }
        User user = new User(input.getFullName(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        userRepository.save(user);
        sendVerificationEmail(user,"Verify your email address");
        return userRepository.findByEmail(user.getEmail()).get();
    }

    public void sendResetPwdCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isEnabled()) {
                throw new ApiRequestException("Account not verified. Please verify your account.");
            }
//            if (user.getVerificationCodeExpiresAt() != null &&
//                    user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
//                throw new RuntimeException("A verification code has already been sent. Please wait before requesting a new one.");
//            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            sendVerificationEmail(user, "Reset your FCHARITY password");
        }else{
            throw new ApiRequestException("Please provide an verificated email!");
        }
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ApiRequestException("User not found"));

        if (!user.isEnabled()) {
            throw new ApiRequestException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public boolean verifyEmail(User user, String verificationCode) {
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiRequestException("Verification code has expired");
        }
        if (user.getVerificationCode().equals(verificationCode)){
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (verifyEmail(user, input.getVerificationCode())) {
                user.setEnabled(true);
                userRepository.save(user);
            } else {
                throw new ApiRequestException("Invalid verification code");
            }
        }else{
            throw new ApiRequestException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new ApiRequestException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user,"Verify your email address");
            userRepository.save(user);
        } else {
            throw new ApiRequestException("User not found");
        }
    }

    private void sendVerificationEmail(User user,String subject) {
        String verificationCode =  user.getVerificationCode();
//        String htmlMessage = "<html>"
//                + "<body style=\"font-family: Arial, sans-serif;\">"
//                + "<div style=\"background-color: #f5f5f5; padding: 20px; display:flex; justify-content: center;align-items: center;\">"
//                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
//                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
//                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
//                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
//                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
//                + "</div>"
//                + "</div>"
//                + "</body>"
//                + "</html>";

        String htmlMessage = "<html>" +
                "<body style=\"min-height: 400px;font-family: Arial, sans-serif; margin: 0; padding: 0;\">" +
                "<div style=\"height: fit-content; font-family: Arial, Helvetica, sans-serif;background-color:#f5f5f5;\">" +
                "<div style=\"width: 33%; margin:0 auto;background-color: white; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" +
                "<div style=\"background-color: black;\">" +
                "<img src=\"https://res.cloudinary.com/dfoq1dvce/image/upload/v1738872035/rvgjtehnunvfb9ysw7sc.png\" alt=\"logo\" style=\"padding: 5px 15px; height: 80px; width: 100px;\">" +
                "</div>" +
                "<div style=\"padding: 30px 40px 50px 40px;\">" +
                "<p style=\"font-size: 28px;padding-top:20px;color:black;\">"+subject+"</p>" +
                "<br/>"+
                "<hr style=\"border: 1px solid rgba(0, 0, 0, 0.1);margin-top:16px;\"/>" +"<br/>"+
                "<div style=\"padding-right: 2rem;padding-top:16px;\">" +
                "<p style=\"font-size: 21px;color:black;\">Please return to your browser window and enter this 6-digit code to reset your password.</p>" +"<br/>"+
                "<b style=\"font-size: 38px;padding:0 26px;\">"+verificationCode+"</b>" +"<br/>"+
                "<p style=\"font-size: 21px;color:black;\">" +"<br/>"+
                "If you did not make this change, please disregard this email. Do not reply to this automated email." +
                "</p>" +
                "</div>" +
                "</div>" +
                "<div style=\"background-color: black; padding: 13px 0px 49px 30px; display: flex; align-items: start;\">" +
                "<p style=\"color: rgba(255, 255, 255, 0.929); text-align: left; font-size: 16px;\">© 2025 FCHARITY All rights reserved</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";


        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    public User resetPassword(ResetPasswordDto resetPasswordDto) {
        User u = userRepository.findByEmail(resetPasswordDto.getEmail()).get();
        try{
            String newPassword = resetPasswordDto.getNewPassword();
            if (passwordEncoder.matches(resetPasswordDto.getNewPassword(), u.getPassword())) {
                throw new ApiRequestException("New password must be different from the old password");
            }
            userService.updatePassword(u.getEmail(), passwordEncoder.encode(newPassword));
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage());
        }
        return u;
    }
}