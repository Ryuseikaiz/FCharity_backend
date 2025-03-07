package fptu.fcharity.controller.authentication;

import fptu.fcharity.dto.authentication.*;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.authentication.AuthenticationService;
import fptu.fcharity.service.authentication.JwtService;
import fptu.fcharity.response.authentication.LoginResponse;
import fptu.fcharity.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/reset-password-otp/{email}")
    public ResponseEntity<?> sendResetPasswordOTP(@PathVariable String email) {
         authenticationService.sendResetPwdCode(email);
        return ResponseEntity.ok("Reset password code sent");
    }
    @PostMapping("/verify-reset-password-otp")
    public ResponseEntity<?> verifyResetPassword(@RequestBody VerifyUserDto verifyUserDto) {
        Optional<User> u = userService.findUserByEmail(verifyUserDto.getEmail());
        return ResponseEntity.ok(authenticationService.verifyEmail(u, verifyUserDto.getVerificationCode()));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return ResponseEntity.ok(authenticationService.resetPassword(resetPasswordDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        authenticationService.verifyUser(verifyUserDto);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/resendOTP")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendOTPDto resendOTPDto) {
            authenticationService.resendVerificationCode(resendOTPDto.getEmail());
            return ResponseEntity.ok(true);

    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        User user = authenticationService.googleLogin(token);
        String jwtToken = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}