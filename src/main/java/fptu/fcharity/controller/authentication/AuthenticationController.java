package fptu.fcharity.controller.authentication;

import fptu.fcharity.dto.authentication.ResetPasswordDto;
import fptu.fcharity.dto.authentication.LoginUserDto;
import fptu.fcharity.dto.authentication.RegisterUserDto;
import fptu.fcharity.dto.authentication.VerifyUserDto;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.UserService;
import fptu.fcharity.service.authentication.AuthenticationService;
import fptu.fcharity.service.authentication.JwtService;
import fptu.fcharity.response.authentication.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
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
        User u = userService.findUserByEmail(verifyUserDto.getEmail());
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
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}